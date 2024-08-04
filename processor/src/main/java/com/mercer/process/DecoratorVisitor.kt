package com.mercer.process

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.validate
import com.mercer.annotate.http.Cache
import com.mercer.annotate.http.Decorator
import com.mercer.annotate.http.JsonKey
import com.mercer.core.Mode
import com.mercer.process.mode.AppendRes
import com.mercer.process.mode.CacheBean
import com.mercer.process.mode.Named
import com.mercer.process.mode.PathRes
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toKModifier
import com.squareup.kotlinpoet.ksp.toTypeName
import retrofit2.http.Body

/**
 * author:  mercer
 * date:    2024/2/15 09:18
 * desc:
 *   Decorator标签解析
 */
class DecoratorVisitor(
    environment: SymbolProcessorEnvironment,
    private val resolver: Resolver,
    private val packageName: String,
    private val apiTypeSpec: TypeSpec.Builder,
    private val implTypeSpec: TypeSpec.Builder,
) : KSVisitorVoid() {

    @Suppress("unused")
    private val logger = environment.logger

    private val apiTypeName: TypeName by lazy {
        val name = apiTypeSpec.build().name!!
        ClassName.bestGuess("$packageName.$name")
    }

    private val implTypeName: TypeName by lazy {
        val name = implTypeSpec.build().name!!
        ClassName.bestGuess("$packageName.$name")
    }

    private lateinit var topAppends: List<AppendRes>
    private val pipelines by lazy { hashSetOf<Pair<TypeName, TypeName>>() }
    private val allNames = arrayListOf<Named>()

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        super.visitClassDeclaration(classDeclaration, data)
        topAppends = classDeclaration.toAppends()
        generateImplSingleton(classDeclaration)
        generateImplProperties(classDeclaration)
        allNames.add(Named(value = "onCreator", flag = Named.GLOBAL_VARIABLE))
        allNames.add(Named(value = "api", flag = Named.GLOBAL_VARIABLE))
        classDeclaration
            .getDeclaredFunctions()
            .filter {
                it.isAbstract
            }
            .filter {
                it.validate()
            }
            .forEach {
                visitFunctionDeclaration(it, Unit)
            }

        logger.warn("classDeclaration  >>> $classDeclaration")

        // 内部所有实现了 OnShared 接口的类/接口
        val innerClassDeclarations = classDeclaration
            .declarations
            .filterIsInstance<KSClassDeclaration>()
            .filter { resolver.isSubClassOfOnShared(it) }
            .toList()

        (innerClassDeclarations.filter {
            it.classKind == ClassKind.INTERFACE
        } + innerClassDeclarations.filter {
            it.classKind == ClassKind.CLASS && it.isAbstract()
        }.filter {
            0 in it.getConstructors().map { f -> f.parameters.size }.toList()
        }).forEach {
            // TODO: 验证是否只有一个抽象函数被注解[PUT DELETE POST GET HTTP] 
            val ksFunctionDeclaration = it.getDeclaredFunctions().first()
            ksFunctionDeclaration.validate()
            val funcName = arrayOf(it, ksFunctionDeclaration).joinToString("_") { e ->
                e.simpleName.getShortName()
            }
            parseFunctionDeclaration(ksFunctionDeclaration, funcName, FLAG_NONE)
        }


        /*
        val sharedKSClassDeclarations = classDeclaration
            .declarations
            .filterIsInstance<KSClassDeclaration>()
            .filter {
                // 为接口 或者 抽象类
                it.classKind == ClassKind.INTERFACE ||
                        (it.classKind == ClassKind.CLASS && it.isAbstract())
            }
            .onEach {
                logger.warn("it.getConstructors() >>> ${it.getConstructors().toList()}")
                logger.warn("it.superTypes >>> ${it.superTypes.toList()}")
                logger.warn("it.isSubClassOfOnShared >>> ${resolver.isSubClassOfOnShared(it)}")
                logger.warn("OnShared::class.asClassName() >>> ${OnShared::class.asClassName()}")
                logger.warn("OnShared::class.asTypeName() >>> ${OnShared::class.asTypeName()}")
            }
            .toList()
            */

    }

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
        super.visitFunctionDeclaration(function, data)
        val funcName = function.simpleName.getShortName()
        parseFunctionDeclaration(function, funcName, FLAG_OVERRIDE)
    }

    @OptIn(KspExperimental::class)
    private fun parseFunctionDeclaration(
        function: KSFunctionDeclaration,
        funcName: String,
        flag: Int
    ) {
        val appends = arrayListOf<AppendRes>()
        appends.addAll(function.toAppends())
        val includes = appends.map { it.toName() }
        appends.addAll(topAppends.filter { it.toName() !in includes })

        val returnType = function.returnType?.toTypeName() ?: Unit::class.asClassName()

        val modifiers = function.modifiers.filter {
            it != Modifier.ABSTRACT
        }.mapNotNull {
            it.toKModifier()
        }

        val caches = function.getAnnotationsByType(Cache::class).toList()
        /*
        var cachePipelineTypeName: TypeName? = null
        var pipelineParameterTypeName: TypeName? = null
        var mode: Mode? = null
        */
        val cacheBean = if (caches.isNotEmpty()) {
            val cache = caches.first()
            val pipeline = parseToTypeName { cache.value }
            val declaration = resolver.getClassDeclarationByName(pipeline.toString())!!
            // TODO: 需要验证 是否存在 无参的构造函数
            val pipelineFunctionReturn = resolver.parsePipelineReturnTypeName(declaration)
            pipelines.add(pipeline to pipelineFunctionReturn)
            val name = Named.produce(allNames, "pipeline")
            val named = Named(name, Named.GLOBAL_VARIABLE or Named.PIPELINE_NAME)
            allNames.add(named)
            CacheBean(cache.mode, pipeline, pipelineFunctionReturn, declaration.classKind, named)
        } else {
            null
        }

        val parameters = function.parameters

        val name = generateApiFunction(
            parameters, appends, funcName, function, modifiers, returnType,
        )

        /*
        private val api by lazy {
            onCreator.create(TestKotlinApi::class)
        }
        */

        if (cacheBean != null) {
            // logger.warn("cacheBean >>> $cacheBean")
            implTypeSpec.addProperty(
                PropertySpec.builder(cacheBean.named.value, cacheBean.pipeline)
                    .addModifiers(KModifier.PRIVATE)
                    .apply {
                        if (cacheBean.classKind == ClassKind.OBJECT) {
                            initializer("%T", cacheBean.pipeline)
                        } else {
                            delegate("lazy { %T() }", cacheBean.pipeline)
                        }
                    }
                    .build()
            )
        }

        val pathRes = function.toPathRes() ?: throw RuntimeException("pathRes is null")

        generateImplFunction(
            pathRes, parameters, funcName, modifiers, returnType, appends, name, cacheBean, flag
        )

    }

    /**
     * 生成静态代理类的单例模式
     */
    private fun generateImplSingleton(classDeclaration: KSClassDeclaration) {
        // 定义超类,实现接口
        implTypeSpec.addSuperinterface(classDeclaration.toClassName())
        // 构造函数私有化
        implTypeSpec.addFunction(
            FunSpec.constructorBuilder().addModifiers(KModifier.PRIVATE).build()
        )
        /*
        companion object {
            operator fun invoke(): TestKotlin {
                return Holder.INSTANCE
            }
            internal inline fun <reified T> suspend2deferred(crossinline block: suspend () -> T): Deferred<T> {
                val deferred = CompletableDeferred<T>()
                runBlocking {
                    deferred.complete(block())
                }
                return deferred
            }
        }
        private object Holder {
            val INSTANCE = TestKotlinImpl()
        }
        */

        /*
        val suspend2DeferredFun = FunSpec.builder("suspend2deferred")
            .addModifiers(KModifier.INTERNAL, KModifier.INLINE)
            .receiver(TypeName.ANY)
            .addTypeVariable(TypeVariableName("T"))
            .addParameter(
                ParameterSpec.builder("block", suspend() -> T::class)
                .addModifiers(KModifier.CROSSINLINE).build()
            )
            .returns(Deferred::class as TypeName)
            .beginControlFlow("val deferred = CompletableDeferred<T>()")
            .beginControlFlow("runBlocking {")
            .addStatement("deferred.complete(block())")
            .endControlFlow()
            .addStatement("return deferred")
            .build()
            */

        //

        val companionObjectTypeSpec = TypeSpec.companionObjectBuilder()
            .addFunction(
                FunSpec
                    .builder("invoke")
                    .addModifiers(KModifier.OPERATOR)
                    // .returns(classDeclaration.toClassName())
                    .returns(implTypeName)
                    .addStatement("return Holder.INSTANCE")
                    .build()
            )

        /*
        companionObjectTypeSpec.addFunction(
            FunSpec
                .builder("suspend2deferred")
                .addModifiers(KModifier.INLINE, KModifier.INTERNAL)
                .addTypeVariable(VARIABLE_NAME_T.copy(reified = true))
                .addParameter(
                    "block", LambdaTypeName
                        .get(returnType = VARIABLE_NAME_T)
                        .copy(suspending = true), KModifier.CROSSINLINE
                )
                .returns(DEFERRED_CLASS_NAME.parameterizedBy(VARIABLE_NAME_T))
                .addCode(
                    buildCodeBlock {
                        addStatement(
                            "val deferred = %T<%N>()",
                            COMPLETABLE_DEFERRED_CLASS_NAME,
                            VARIABLE_NAME_T.name
                        )
                        beginControlFlow("%M ", RUN_BLOCKING_FLOW_FUNCTION)
                        addStatement("deferred.complete(block())")
                        endControlFlow()
                        addStatement("return deferred")
                    }
                )
                .build()
        )
        */

        implTypeSpec.addType(companionObjectTypeSpec.build())
        implTypeSpec.addType(
            TypeSpec.objectBuilder("Holder")
                .addModifiers(KModifier.PRIVATE)
                .addProperty(
                    PropertySpec
                        // .builder("INSTANCE", classDeclaration.toClassName())
                        .builder("INSTANCE", implTypeName)
                        .initializer("%T()", implTypeName)
                        .build()
                )
                .build()
        )
    }

    /**
     * 生成静态代理类的成员变量
     */
    @OptIn(KspExperimental::class)
    private fun generateImplProperties(classDeclaration: KSClassDeclaration) {
        val typeName = parseToTypeName {
            classDeclaration.getAnnotationsByType(Decorator::class).first().value
        }
        /*
        private val onCreator by lazy {
            SimpleCreator()
        }
         */
        PropertySpec.builder("onCreator", CREATOR_CLASS_NAME)
            .addModifiers(KModifier.PRIVATE)
            .delegate("lazy {%T()}", typeName)
            .build()
            .let {
                implTypeSpec.addProperty(it)
            }

        /*
        private val api by lazy {
            onCreator.create(TestKotlinApi::class)
        }
         */
        val apiClassName = apiTypeName
        PropertySpec.builder("api", apiClassName)
            .addModifiers(KModifier.PRIVATE)
            .delegate("lazy { onCreator.create(%T::class)}", apiClassName)
            .build()
            .let {
                implTypeSpec.addProperty(it)
            }

    }

    /**
     * 生成 Retrofit 所需要的动态代理的接口类的接口方法
     */
    private fun generateApiFunction(
        parameters: List<KSValueParameter>,
        appends: ArrayList<AppendRes>,
        name: String,
        function: KSFunctionDeclaration,
        modifiers: List<KModifier>,
        returnType: TypeName,
    ): String {
        val parameterSpecs = parameters.filter(HAS_RETROFIT).mapIndexed { i, p ->
            val pType: TypeName = p.type.toTypeName()
            val pName = "v${i + 1}"
            ParameterSpec.builder(pName, pType)
                .addAnnotations(p.toAnnotationSpecs(RETROFIT))
                .build()
        }.toMutableList()
        val names = arrayListOf<Named>()
        names.addAll(parameterSpecs.map { Named(value = it.name, flag = Named.PARAMETER) })

        val hasJsonKey = parameters.any(HAS_JSON_KEY)
        if (hasJsonKey) {
            val produce = Named.produce(names)
            names.add(Named(produce, Named.VARIABLE))
            parameterSpecs.add(
                ParameterSpec.builder(produce, ANY).addAnnotation(Body::class).build()
            )
        }
        parameterSpecs.addAll(
            appends.mapIndexed { i, e ->
                val (value, annotation, memberFormat) = e
                val pName = "v${i + names.size + 1}"
                ParameterSpec
                    .builder(pName, ANY_NULLABLE)
                    .addAnnotation(
                        AnnotationSpec
                            .builder(annotation)
                            .addMember(memberFormat ?: "", value ?: "")
                            .build()
                    )
                    .build()
            }
        )
        val unique = StringBuilder().apply {
            for (spec in parameters) {
                append(spec.type)
                append(",")
            }
        }.toString().md5().let {
            "${name}_$it"
        }
        val funSpec = FunSpec.builder(unique)
            .addAnnotations(function.toAnnotationSpecs(RETROFIT))
            .addModifiers(modifiers)
            .addModifiers(KModifier.ABSTRACT)
            .addParameters(parameterSpecs)
            .apply {
                val isCoroutines =
                    returnType is ParameterizedTypeName && returnType.rawType in COROUTINES
                if (isCoroutines) {
                    addModifiers(KModifier.SUSPEND)
                    returns((returnType as ParameterizedTypeName).typeArguments.first())
                } else {
                    returns(returnType)
                }
            }
            .build()
        apiTypeSpec.addFunction(funSpec)
        return funSpec.name
    }

    /**
     * 生成 静态代理类的 方法
     */
    @OptIn(KspExperimental::class)
    private fun generateImplFunction(
        pathRes: PathRes,
        parameters: List<KSValueParameter>,
        funcName: String,
        modifiers: List<KModifier>,
        returnType: TypeName,
        appends: ArrayList<AppendRes>,
        name: String,
        cacheBean: CacheBean? = null,
        flag: Int
    ) {
        val names = arrayListOf<Named>()
        names.addAll(parameters.map {
            val flag = if (it.any(JSON_KEY)) Named.TEMPORARY else Named.PARAMETER
            Named(value = it.name!!.getShortName(), flag = flag)
        })
        names.addAll(allNames)
        val jsonKeys = parameters.filter(HAS_JSON_KEY)

        val funSpecBuilder = FunSpec.builder(funcName)
            .addModifiers(modifiers)
            .apply {
                if (flag intersect FLAG_OVERRIDE) {
                    addModifiers(KModifier.OVERRIDE)
                }
            }
            .returns(returnType)
            .addParameters(parameters.map {
                val pType: TypeName = it.type.toTypeName()
                val pName = it.name?.getShortName()!!
                ParameterSpec.builder(pName, pType).build()
            })

        if (jsonKeys.isNotEmpty()) {
            val named = Named(value = Named.produce(names), flag = Named.VARIABLE)
            names.add(named)
            funSpecBuilder.addStatement("val %N = hashMapOf<String, Any?>()", named.value)
            for (jsonKey in jsonKeys) {
                val k = jsonKey.getAnnotationsByType(JsonKey::class).first().value
                val v = jsonKey.name!!.asString()
                // funSpecBuilder.addStatement("%N.put(%S,%N)", named.value, k, v)
                if (jsonKey.type.toTypeName() in arrayOf(STRING, STRING_NULLABLE)) {
                    funSpecBuilder.addStatement("%N[%S]=%N", named.value, k, v)
                } else {
                    funSpecBuilder.addStatement("%N[%S]=%N", named.value, k, v)
                }
            }
        }
        /*
         val v1 = MyStringProvider4().provide("kkk")
         */
        val (pathTypeName, path) = pathRes

        if (cacheBean != null || appends.isNotEmpty()) {
            val pathNamed = Named(Named.produce(names), Named.TEMPORARY or Named.PATH_NAME)
            names.add(pathNamed)
            funSpecBuilder.addStatement("val %N = %T(%S)", pathNamed.value, pathTypeName, path)
        }
        if (appends.isNotEmpty()) {
            val pathNamed = names.find { it.flag intersect Named.PATH_NAME }!!
            for (res in appends) {
                val named = Named(Named.produce(names), Named.VARIABLE or Named.TEMPORARY)
                names.add(named)
                funSpecBuilder.addStatement(
                    "val %N = %T().provide(%N,%S)",
                    named.value, res.provider, pathNamed.value, res.name ?: ""
                )
            }
        }
        val returnRawType = if (returnType is ParameterizedTypeName) {
            returnType.rawType
        } else {
            returnType
        }

        val args = buildString {
            val values = names.filter {
                it.flag intersect Named.PARAMETER || it.flag intersect Named.VARIABLE
            }
            for (index in values.indices) {
                val named = values[index]
                append("v${index + 1} = ${named.value}, ")
                if (index < values.size - 1) {
                    append(WRAP)
                }
            }
        }
        when (returnRawType) {
            FLOW_CLASS_NAME -> {
                if (cacheBean == null) {
                    funSpecBuilder.addCode(buildCodeBlock {
                        beginControlFlow("return %M", FLOW_FUNCTION)
                        addStatement("emit(api.%N(%L))", name, args)
                        endControlFlow()
                    })
                } else {
                    val (mode, pipelineT, pipelineReturn, classKind, named) = cacheBean
                    val pathNamed = names.find { it.flag intersect Named.PATH_NAME }!!
                    funSpecBuilder.addCode(buildCodeBlock {
                        beginControlFlow(
                            "return %T.%L(%N,%N)", Mode::class, mode, pathNamed.value, named.value
                        )
                        addStatement("api.%N(%L)", name, args)
                        endControlFlow()
                    })
                }
            }

            DEFERRED_CLASS_NAME -> {
                funSpecBuilder.addStatement(
                    "return %M{\rapi.%N(%L)\n}", SUSPEND_2_DEFERRED_FUNCTION, name, args
                )
            }

            else -> {
                funSpecBuilder.addStatement("return api.%N(%L)", name, args)
            }
        }
        implTypeSpec.addFunction(funSpecBuilder.build())
    }

}