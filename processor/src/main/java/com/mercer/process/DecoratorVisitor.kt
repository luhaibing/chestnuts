package com.mercer.process

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.validate
import com.mercer.annotate.http.Cache
import com.mercer.annotate.http.Decorator
import com.mercer.annotate.http.JsonKey
import com.mercer.annotate.http.State
import com.mercer.core.Strategy.DEFAULT
import com.mercer.process.mode.AppendRes
import com.mercer.process.mode.CacheRes
import com.mercer.process.mode.Named
import com.mercer.process.mode.PathRes
import com.mercer.process.mode.StateRes
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toKModifier
import com.squareup.kotlinpoet.ksp.toTypeName
import retrofit2.http.Body

/**
 * author:  Mercer
 * date:    2024/8/10
 * desc:
 *   Decorator 标签解析
 */
class DecoratorVisitor(
    private val env: SymbolProcessorEnvironment,
    private val resolver: Resolver,
    private val pipelineTypeNames: MutableMap<TypeName, Named>,
    private val apiTypeSpec: TypeSpec.Builder,
    private val implTypeSpec: TypeSpec.Builder,
) : KSVisitorVoid() {

    private val logger: KSPLogger = env.logger
    private val names = arrayListOf<Named>()
    private val topAppends: MutableList<AppendRes> = arrayListOf()

    @OptIn(KspExperimental::class)
    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        super.visitClassDeclaration(classDeclaration, data)
        // 构造函数私有化
        implTypeSpec.primaryConstructor(
            FunSpec.constructorBuilder().addModifiers(KModifier.PRIVATE).build()
        )
        // 抽象方法
        val absFunctions = classDeclaration.getDeclaredFunctions()
            .filter { it.validate() && it.isAbstract }
            .onEach { f ->
                if (f.toPathRes().count() != 1) {
                    // 必须有且只有 [PUT, DELETE, POST, GET] 其中一个注解
                    logger.error("${f.qualifiedName?.asString()} : There must be only one of [PUT, DELETE, POST, GET] annotations.")
                }
            }
            .onEach {
                val cacheRes = it.toCacheRes(resolver)
                if (cacheRes != null) {
                    val returnTypeTypeName = it.returnType?.toTypeName() ?: UNIT
                    val rt = (returnTypeTypeName as? ParameterizedTypeName)?.rawType ?: returnTypeTypeName
                    if (rt != FLOW_CLASS_NAME) {
                        logger.error("${it.qualifiedName?.asString()} : The outermost layer of the return value is not Flow.")
                    }
                    // rawReturnTypeName
                    val rawReturnTypeName = (returnTypeTypeName as? ParameterizedTypeName)
                        ?.typeArguments?.first()?.copy(nullable = true)
                    if (rawReturnTypeName != cacheRes.pipelineParameterizedTypeName.copy(nullable = true)) {
                        logger.error("${it.qualifiedName?.asString()} : The entity type is inconsistent.")
                    }
                }
            }
            .toList()

        // 使用了 Shared 注解的内部类
        val sharedClasses = classDeclaration.declarations
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.validate() && (it.classKind == ClassKind.INTERFACE || it.isAbstract()) }
            .filter {
                val hasAnnotation = it.getAnnotationsByType(State::class).count() > 0
                val isSubClassOf = it.isSubClassOf(ON_STATE_CLASS_NAME)
                hasAnnotation || isSubClassOf
            }
            .onEach {
                val isSubClassOf = it.isSubClassOf(ON_STATE_CLASS_NAME)
                val stateRes = it.toStateRes(resolver)
                val hasAnnotation = stateRes != null
                if (hasAnnotation != isSubClassOf) {
                    // 必须实现 OnState 接口, 并且有 @State 注解
                    logger.error("${it.qualifiedName?.asString()} : The $ON_STATE_CLASS_NAME interface must be implemented with the @${State::class.qualifiedName} annotation.")
                }
                val pipelineParameterizedTypeName = stateRes?.pipelineParameterizedTypeName ?: UNIT
                val returnTypeNames = it.getStateTargetTheReturnValueOfInterfaceFunction()
                val condition = returnTypeNames.any { t ->
                    val rt = (t as? ParameterizedTypeName)?.rawType ?: t
                    rt != FLOW_CLASS_NAME
                }
                if (condition) {
                    // 返回值最外层不是Flow
                    logger.error("${it.qualifiedName?.asString()} : The outermost layer of the return value is not Flow.")
                }
                // 实体类型都一致(不区分是否可空)
                val rawReturnTypeNames = returnTypeNames.mapNotNull { t ->
                    (t as? ParameterizedTypeName)?.typeArguments?.first()?.copy(nullable = true)
                }.toSet()
                if (rawReturnTypeNames.size != 1) {
                    // 实体类型不一致
                    logger.error("${it.qualifiedName?.asString()} : Function returns entity type inconsistent.")
                }
                val rawReturnTypeName = rawReturnTypeNames.firstOrNull() ?: UNIT
                if (pipelineParameterizedTypeName.copy(nullable = true) != rawReturnTypeName.copy(nullable = true)) {
                    // 函数返回实体类型和Pipeline参数化类型不一致
                    logger.error("${it.qualifiedName?.asString()} : The entity type returned by the function does not match the Pipeline parameterized type.")
                }
            }
            .toList()

        if (absFunctions.isEmpty() && sharedClasses.isEmpty()) {
            return
        }

        // 顶层的追加参数
        topAppends.addAll(classDeclaration.toAppends(resolver))

        // 记录所有属性和方法的名字
        classDeclaration.declarations.forEach { d ->
            when (d) {
                is KSFunctionDeclaration -> Named(d.simpleName.asString(), Named.TYPE_FUNCTION)
                is KSPropertyDeclaration -> Named(d.simpleName.asString(), Named.TYPE_PROPERTY)
                else -> null
            }?.let {
                names.add(it)
            }
        }

        // 统计所有的 Pipeline,并分配名字
        (absFunctions.mapNotNull {
            it.getAnnotation(Cache::class)?.toTypeName { pipeline }
        } + sharedClasses.mapNotNull {
            it.getAnnotation(State::class)?.toTypeName { value }
        }).asSequence().filter {
            it !in pipelineTypeNames.keys
        }.forEach {
            val name = Named.produce(names, "pipeline")
            val named = Named(name, Named.TYPE_PROPERTY or Named.NAME_PIPELINE)
            pipelineTypeNames[it] = named
            names.add(named)
        }
        // 实现接口/继承超类
        if (classDeclaration.classKind == ClassKind.INTERFACE) {
            implTypeSpec.addSuperinterface(classDeclaration.toClassName())
        } else {
            implTypeSpec.superclass(classDeclaration.toClassName())
        }
        // 最外层的类
        if (classDeclaration.parentDeclaration == null) {
            implTypeSpec.addTypes(classDeclaration.generateImplObjects())
            implTypeSpec.addProperties(classDeclaration.generateImplProperties())
        }
        absFunctions.forEach {
            visitFunctionDeclaration(it, Unit)
        }
        sharedClasses.forEach {
            val innerImplTypeSpec = TypeSpec.classBuilder(it.implName).addModifiers(KModifier.INNER)
            val pipelineTypeName = it.getAnnotation(State::class)!!.toTypeName { value }

            innerImplTypeSpec.addProperty(
                PropertySpec.builder("pipeline", pipelineTypeName)
                    .apply {
                        val named = pipelineTypeNames[pipelineTypeName]!!
                        initializer("%N", named.value)
                    }
                    .addModifiers(KModifier.OVERRIDE)
                    .build()
            )

            // 类型参数
            val typeParameter = it.getTypeParameterOf(ON_STATE_CLASS_NAME)!!

            // 查询是否重写了 defaultValue 方法
            val defaultValueFunc = it.getDeclaredFunctions().find { f ->
                f.simpleName.asString() == ON_STATE_DEFAULT_VALUE_FUNCTION
            }
            val valueTypeName = typeParameter.copy(nullable = true)
            innerImplTypeSpec.addProperty(
                PropertySpec
                    .builder(
                        ON_STATE_INNER_CURRENT_FLOW,
                        MUTABLE_STATE_FLOW_CLASS_NAME.parameterizedBy(valueTypeName)
                    )
                    .addModifiers(KModifier.PRIVATE)
                    .apply {
                        if (defaultValueFunc == null) {
                            initializer("%T(null)", MUTABLE_STATE_FLOW_CLASS_NAME)
                        } else {
                            initializer(
                                "%T(%N())",
                                MUTABLE_STATE_FLOW_CLASS_NAME, ON_STATE_DEFAULT_VALUE_FUNCTION
                            )
                        }
                    }
                    .build()
            )
            innerImplTypeSpec.addProperty(
                PropertySpec
                    .builder("currentFlow", STATE_FLOW_CLASS_NAME.parameterizedBy(valueTypeName))
                    .initializer(ON_STATE_INNER_CURRENT_FLOW)
                    .addModifiers(KModifier.OVERRIDE)
                    .build()
            )

            // 获取第一个抽象方法的注解
            val pathRes = it.getDeclaredFunctions()
                .filter { f -> f.validate() && f.isAbstract }
                .mapNotNull { f ->
                    f.toPathRes().firstOrNull()
                }.first()

            innerImplTypeSpec.addProperty(
                PropertySpec
                    .builder("path", PATH_CLASS_NAME)
                    .initializer("%T(%S)", pathRes.typeName, pathRes.value)
                    .addModifiers(KModifier.OVERRIDE)
                    .build()
            )

            innerImplTypeSpec.addInitializerBlock(
                buildCodeBlock {
                    beginControlFlow("onCreator.scope.%M", LAUNCH_FUNCTION_NAME)
                    addStatement("val value = pipeline.read(path)")
                    beginControlFlow("value?.let")
                    addStatement("%N.value = it", ON_STATE_INNER_CURRENT_FLOW)
                    endControlFlow()
                    endControlFlow()
                }
            )
            it.accept(DecoratorVisitor(env, resolver, pipelineTypeNames, apiTypeSpec, innerImplTypeSpec))
            implTypeSpec.addType(innerImplTypeSpec.build())
        }
    }

    /**
     * 生成 companion object 类 和 object 类
     */
    private fun KSClassDeclaration.generateImplObjects(): List<TypeSpec> {
        return arrayListOf<TypeSpec>().apply {
            /*
             companion object {
               operator fun invoke(): TestKotlinImpl = Holder.INSTANCE
             }
             */
            add(
                TypeSpec.companionObjectBuilder()
                    .addFunction(
                        FunSpec.builder("invoke")
                            .addModifiers(KModifier.OPERATOR)
                            .returns(implTypeName)
                            .addStatement("return Holder.INSTANCE")
                            .build()
                    )
                    .build()
            )
            /*
             private object Holder {
               val INSTANCE: TestKotlinImpl = TestKotlinImpl()
             }
            */
            add(
                TypeSpec.objectBuilder("Holder")
                    .addModifiers(KModifier.PRIVATE)
                    .addProperty(
                        PropertySpec
                            .builder("INSTANCE", implTypeName)
                            .initializer("%T()", implTypeName)
                            .build()
                    )
                    .build()
            )
        }
    }

    /**
     * 生成 必须的属性
     */
    private fun KSClassDeclaration.generateImplProperties(): List<PropertySpec> {
        return arrayListOf<PropertySpec>().apply {
            /*
            private val onCreator by lazy {
                SimpleCreator()
            }
            */
            val typeName = getAnnotation(Decorator::class)!!.toTypeName { value }
            add(
                PropertySpec.builder("onCreator", CREATOR_CLASS_NAME)
                    .addModifiers(KModifier.PRIVATE)
                    .delegate(buildCodeBlock {
                        beginControlFlow("lazy")
                        add("%T()", typeName)
                        endControlFlow()
                    })
                    .build()
            )
            /*
            private val api by lazy {
                onCreator.create(TestKotlinApi::class)
            }
            */
            add(
                PropertySpec.builder("api", apiTypeName)
                    .addModifiers(KModifier.PRIVATE)
                    .delegate(buildCodeBlock {
                        beginControlFlow("lazy")
                        addStatement("onCreator.create(%T::class)", apiTypeName)
                        endControlFlow()
                    })
                    .build()
            )
            names.add(Named("onCreator", Named.TYPE_VARIABLE))
            names.add(Named("api", Named.TYPE_VARIABLE))
        }
    }

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
        super.visitFunctionDeclaration(function, data)
        val fNames = arrayListOf<Named>()
        fNames.addAll(names)
        val pathRes = function.toPathRes().first()
        val appends = arrayListOf<AppendRes>().apply {
            for (res in function.toAppends(resolver).toList().reversed()) {
                val uniques = map { it.unique }
                val unique = res.unique
                if (unique in uniques) continue
                if (res.flags.any { pathRes.flag intersect it }.not()) continue
                add(res)
            }
            for (res in topAppends.reversed()) {
                val uniques = map { it.unique }
                val unique = res.unique
                if (unique in uniques) continue
                if (!res.flags.any { pathRes.flag intersect it }) continue
                add(res)
            }
        }
        appends.reverse()
        val apiFunction = function.generateApiFunction(appends)
        val implFunction = function.generateImplFunction(appends, pathRes, apiFunction.name)
        apiTypeSpec.addFunction(apiFunction)
        implTypeSpec.addFunction(implFunction)
    }

    /**
     * 生成 Retrofit 所需要的动态代理的接口类的接口方法
     */
    private fun KSFunctionDeclaration.generateApiFunction(appends: List<AppendRes>): FunSpec {
        val parameterSpecs = mutableListOf<ParameterSpec>()
        val returnType = returnType?.toTypeName() ?: UNIT
        val apiReturnType = if ((returnType as? ParameterizedTypeName)?.rawType in COROUTINES) {
            (returnType as ParameterizedTypeName).typeArguments.first()
        } else {
            returnType
        }
        val ns = arrayListOf<Named>()
        parameterSpecs.addAll(parameters.filter(hasRetrofit).mapIndexed { i, p ->
            val pType: TypeName = p.type.toTypeName()
            val pName = "v${i + 1}"
            ParameterSpec.builder(pName, pType)
                .addAnnotations(p.toAnnotationSpecs(RETROFIT))
                .build()
        }.onEach {
            ns.add(Named(it.name, Named.TYPE_VARIABLE))
        })
        if (parameters.size != parameterSpecs.size) {
            val name = Named.produce(ns, "v")
            ns.add(Named(name, Named.TYPE_VARIABLE))
            parameterSpecs.add(
                ParameterSpec.builder(name, ANY).addAnnotation(Body::class).build()
            )
        }
        for (res in appends) {
            val name = Named.produce(ns, "v")
            ns.add(Named(name, Named.TYPE_VARIABLE))
            parameterSpecs.add(
                ParameterSpec.builder(name, res.returnTypeName)
                    .addAnnotation(
                        AnnotationSpec.builder(res.annotation)
                            .addMember("value = %S", res.key)
                            .build()
                    ).build()
            )
        }
        return FunSpec
            .builder(arrayOf(simpleName.asString(), signature.md5).joinToString("_"))
            .addModifiers(KModifier.ABSTRACT, KModifier.SUSPEND)
            .addAnnotations(toAnnotationSpecs(RETROFIT))
            .addKdoc(signature)
            .returns(apiReturnType)
            .addParameters(parameterSpecs.asIterable())
            .build()
    }

    /**
     * 生成 原接口 类的静态代理 方法
     */
    private fun KSFunctionDeclaration.generateImplFunction(
        appends: List<AppendRes>, pathRes: PathRes, apiFunc: String,
    ): FunSpec {
        val modifiers =
            modifiers.mapNotNull { m -> m.toKModifier() }.filter { it != KModifier.ABSTRACT }
        val returnType = returnType?.toTypeName() ?: UNIT
        val ns = arrayListOf<Named>()
        ns.addAll(parameters.mapNotNull { p ->
            val f = if (p.hasAnnotation(RETROFIT)) Named.TYPE_PARAMETER else Named.TYPE_TEMPORARY
            val value = p.name?.asString() ?: return@mapNotNull null
            Named(value, f)
        })
        val jsonKeyParameters = parameters.filter(hasJsonKey)
        val cacheRes: CacheRes?
        val stateRes: StateRes?
        if (parentDeclaration?.parentDeclaration == null) {
            cacheRes = toCacheRes(resolver)
            stateRes = null
        } else {
            cacheRes = null
            stateRes = parentDeclaration?.toStateRes(resolver)
        }
        return FunSpec.builder(simpleName.asString())
            .addModifiers(modifiers)
            .addModifiers(KModifier.OVERRIDE)
            .returns(returnType)
            .addParameters(parameters.mapNotNull { p ->
                val name = p.name?.asString() ?: return@mapNotNull null
                ParameterSpec(name, p.type.toTypeName())
            })
            .apply {
                if (jsonKeyParameters.isNotEmpty()) {
                    val n = Named.produce(ns, "v")
                    ns.add(Named(n, Named.TYPE_VARIABLE or Named.NAME_BODY))
                    addComment("wrap data that is passed using json.")
                    addStatement("val %N = hashMapOf<%T, %T>()", n, STRING, ANY_NULLABLE)
                    for (parameter in jsonKeyParameters) {
                        val jsonKey = parameter.getAnnotation(JsonKey::class) ?: continue
                        val k = jsonKey.value
                        val v = parameter.name?.asString() ?: continue
                        addStatement("%N[%S]=%N", n, k, v)
                    }
                }
                if (cacheRes != null || appends.isNotEmpty()) {
                    val n = Named.produce(ns, "v")
                    ns.add(Named(n, Named.TYPE_TEMPORARY or Named.NAME_PATH))
                    addStatement("val %N = %T(%S)", n, pathRes.typeName, pathRes.value)
                }
                for (res in appends) {
                    val n = Named.produce(ns, "v")
                    ns.add(Named(n, Named.TYPE_VARIABLE))
                    val pn = ns.find { it.flag intersect Named.NAME_PATH } ?: continue
                    val providerTypeName = res.providerTypeName
                    val value = pn.value
                    val key = res.key
                    addStatement("val %N = %T().provide(%N,%S)", n, providerTypeName, value, key)
                }
            }
            .addCode(buildCodeBlock {
                val args = buildString {
                    val values = ns.filter {
                        it.flag intersect Named.TYPE_PARAMETER || it.flag intersect Named.TYPE_VARIABLE
                    }
                    for (index in values.indices) {
                        val named = values[index]
                        append("v${index + 1} = ${named.value}, ")
                        // if (index < values.size - 1) append(WRAP)
                    }
                }
                val returnRawType = if (returnType is ParameterizedTypeName) {
                    returnType.rawType
                } else {
                    returnType
                }
                when (returnRawType) {
                    FLOW_CLASS_NAME -> {
                        if (cacheRes != null) {
                            val (pipelineTypeName, _, strategy) = cacheRes
                            val pn = ns.find { it.flag intersect Named.NAME_PATH }!!
                            val named = pipelineTypeNames[pipelineTypeName]!!
                            addStatement(
                                "return %M({",
                                if (strategy == DEFAULT) DEFAULT_CACHE_USE_STRATEGY else SELECT_CACHE_USE_STRATEGY
                            )
                            addStatement("api.%N(%L)", apiFunc, args)
                            addStatement("}, {")
                            addStatement("%N.read(%N)", named.value, pn.value)
                            addStatement("}, {")
                            addStatement("%N.write(%N,it)", named.value, pn.value)
                            addStatement("})")
                        } else if (stateRes != null) {
                            beginControlFlow("return %M", FLOW_FUNCTION)
                            addStatement("emit(api.%N(%L))", apiFunc, args)
                            endControlFlow()
                            beginControlFlow(".%M", ON_EACH_FUNCTION)
                            addStatement("%N.value = it", ON_STATE_INNER_CURRENT_FLOW)
                            endControlFlow()
                            beginControlFlow(".%M", ON_EACH_FUNCTION)
                            addStatement("pipeline.write(path, it)")
                            endControlFlow()
                        } else {
                            beginControlFlow("return %M", FLOW_FUNCTION)
                            addStatement("emit(api.%N(%L))", apiFunc, args)
                            endControlFlow()
                        }
                    }

                    DEFERRED_CLASS_NAME -> {
                        beginControlFlow("return %M", SUSPEND_2_DEFERRED_FUNCTION)
                        add("api.%N(%L)", apiFunc, args)
                        endControlFlow()
                    }

                    else -> {
                        addStatement("return api.%N(%L)", apiFunc, args)
                    }
                }
            }).build()
    }

    /**
     * 获取 @State 的接口类 接口方法的返回值
     */
    private fun KSClassDeclaration.getStateTargetTheReturnValueOfInterfaceFunction(): Sequence<TypeName> {
        return getDeclaredFunctions()
            .filter { f -> f.validate() && f.isAbstract }
            .filter { f -> f.toPathRes().count() == 1 }
            .map { f -> f.returnType?.toTypeName() ?: UNIT }
    }

}
