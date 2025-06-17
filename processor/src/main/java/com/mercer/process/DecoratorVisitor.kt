package com.mercer.process

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.validate
import com.mercer.annotate.http.CacheKey
import com.mercer.annotate.http.Decorator
import com.mercer.annotate.http.JsonKey
import com.mercer.process.Core.CACHE_KEYS_CLASS_NAME
import com.mercer.process.Core.CREATOR_CLASS_NAME
import com.mercer.process.Coroutines.COMPLETABLE_DEFERRED_CLASS_NAME
import com.mercer.process.Coroutines.COROUTINES
import com.mercer.process.Coroutines.DEFERRED_CLASS_NAME
import com.mercer.process.Coroutines.FLOW_CLASS_NAME
import com.mercer.process.Coroutines.FLOW_FUNCTION
import com.mercer.process.Coroutines.LAUNCH_FLOW_FUNCTION
import com.mercer.process.Kotlin.ANY_NULLABLE
import com.mercer.process.Kotlin.TYPE_OF_NAME
import com.mercer.process.Variable.API_NAME
import com.mercer.process.Variable.CONVERTERS_NAME
import com.mercer.process.Variable.CREATOR_NAME
import com.mercer.process.mode.AppendRes
import com.mercer.process.mode.Named
import com.mercer.process.mode.PathRes
import com.mercer.process.mode.SerializationRes
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
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
import retrofit2.http.Path

/**
 * author:  Mercer
 * date:    2024/8/10
 * desc:
 *   Decorator 标签解析
 */
class DecoratorVisitor(
    env: SymbolProcessorEnvironment,
    private val resolver: Resolver,
    private val apiTypeSpec: TypeSpec.Builder,
    private val implTypeSpec: TypeSpec.Builder,
    private val classDeclaration: KSClassDeclaration,
) : KSVisitorVoid() {

    private val logger: KSPLogger = env.logger
    private val memberNames = arrayListOf<Named>()
    private val memberAppends: MutableList<AppendRes> = arrayListOf()
    private var memberSerializationType: SerializationRes? = null

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        super.visitClassDeclaration(classDeclaration, data)
        apiTypeSpec.addAnnotations(classDeclaration.toAnnotationSpecs(CORE_ANNOTATIONS_EXCLUDE))
        // 构造函数私有化
        implTypeSpec.primaryConstructor(FunSpec.constructorBuilder().addModifiers(KModifier.PRIVATE).build())
        // 抽象方法
        val absFunctions = classDeclaration.getDeclaredFunctions()
            .filter { it.validate() && it.isAbstract }
            .onEach { f ->
                if (f.toPathRes().count() != 1) {
                    // logger.error("${f.qualifiedName?.asString()} must have one of [PUT, DELETE, POST, GET] annotations.")
                    throw IllegalArgumentException("${f.qualifiedName?.asString()} must have one of [PUT, DELETE, POST, GET] annotations.")
                }
            }
            .toList()

        if (absFunctions.isEmpty()) {
            return
        }

        // 顶层的追加参数
        memberAppends.addAll(classDeclaration.toAppends(resolver))

        // 记录所有属性和方法的名字
        classDeclaration.declarations.forEach { d ->
            when (d) {
                is KSFunctionDeclaration -> Named(d.simpleName.asString(), Named.TYPE_FUNCTION)
                is KSPropertyDeclaration -> Named(d.simpleName.asString(), Named.TYPE_PROPERTY)
                else -> null
            }?.let {
                memberNames.add(it)
            }
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
        memberSerializationType = classDeclaration.toSerializationRes(resolver)
        absFunctions.forEach {
            visitFunctionDeclaration(it, Unit)
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
            // 判断是否有无参构造/条件
            typeName.requireConstructor(resolver)
            add(
                PropertySpec.builder(CREATOR_NAME, CREATOR_CLASS_NAME)
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
                PropertySpec.builder(API_NAME, apiTypeName)
                    .addModifiers(KModifier.PRIVATE)
                    .delegate(buildCodeBlock {
                        beginControlFlow("lazy")
                        addStatement("onCreator.create(%T::class)", apiTypeName)
                        endControlFlow()
                    })
                    .build()
            )
            memberNames.add(Named(CREATOR_NAME, Named.TYPE_VARIABLE))
            memberNames.add(Named(API_NAME, Named.TYPE_VARIABLE))

            add(
                PropertySpec.builder(CONVERTERS_NAME, Kotlin.CONVERTER_FACTORY_NAME)
                    .addModifiers(KModifier.PRIVATE)
                    .delegate(buildCodeBlock {
                        beginControlFlow("lazy")
                        addStatement("%T()", Kotlin.CONVERTER_FACTORY_NAME)
                        endControlFlow()
                    })
                    .build()
            )
            memberNames.add(Named(CONVERTERS_NAME, Named.TYPE_VARIABLE))
        }
    }

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
        super.visitFunctionDeclaration(function, data)
        val pathRes = function.toPathRes().first()
        val appends = arrayListOf<AppendRes>().apply {
            for (res in function.toAppends(resolver).toList().reversed()) {
                val uniques = map { it.unique }
                val unique = res.unique
                if (unique in uniques) continue
                if (res.flags.any { pathRes.flag intersect it }.not()) continue
                add(res)
            }
            for (res in memberAppends.reversed()) {
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
        val kModifiers = hashSetOf<KModifier>()
        val apiReturnType = if ((returnType as? ParameterizedTypeName)?.rawType in COROUTINES) {
            kModifiers.add(KModifier.SUSPEND)
            (returnType as ParameterizedTypeName).typeArguments.first()
        } else {
            returnType
        }
        kModifiers.add(KModifier.ABSTRACT)
        kModifiers.addAll(modifiers.mapNotNull(Modifier::toKModifier))
        val parameterNames = arrayListOf<Named>()
        parameterSpecs.addAll(parameters.filter(hasRetrofit).mapIndexed { i, p ->
            val pType: TypeName = p.type.toTypeName()
            val pName = "v${i + 1}"
            ParameterSpec.builder(pName, pType)
                .addAnnotations(p.toAnnotationSpecs(CORE_ANNOTATIONS_EXCLUDE))
                .build()
        }.onEach {
            parameterNames.add(Named(it.name, Named.TYPE_VARIABLE))
        })
        if (parameters.any(hasJsonKey)) {
            val name = Named.produce(parameterNames, "v")
            parameterNames.add(Named(name, Named.TYPE_VARIABLE))
            parameterSpecs.add(ParameterSpec.builder(name, ANY).addAnnotation(Body::class).build())
        }
        for (res in appends) {
            val name = Named.produce(parameterNames, "v")
            parameterNames.add(Named(name, Named.TYPE_VARIABLE))
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
            .addModifiers(kModifiers)
            .addAnnotations(toAnnotationSpecs(CORE_ANNOTATIONS_EXCLUDE))
            .addKdoc(signature)
            .returns(apiReturnType)
            .addParameters(parameterSpecs.asIterable())
            .build()
    }

    /**
     * 生成 原接口 类的静态代理 方法
     */
    private fun KSFunctionDeclaration.generateImplFunction(appends: List<AppendRes>, pathRes: PathRes, apiFunc: String): FunSpec {
        val modifiers = modifiers.mapNotNull { m -> m.toKModifier() }.filter { it != KModifier.ABSTRACT }
        val returnTypeName = returnType?.toTypeName() ?: UNIT
        val returnRawTypeName = returnTypeName.rawType
        val returnApiTypeName =
            if (returnRawTypeName in COROUTINES) (returnTypeName as ParameterizedTypeName).typeArguments.first() else returnTypeName
        val names = arrayListOf<Named>()
        names.addAll(parameters.mapNotNull { p ->
            val f = if (p.hasAnnotation(RETROFIT)) Named.TYPE_PARAMETER else Named.TYPE_TEMPORARY
            val value = p.name?.asString() ?: return@mapNotNull null
            Named(value, f)
        })

        val jsonKeyParameters = parameters.filter(hasJsonKey)
        val pathParameters = parameters.filter(hasPath)
        val cacheKeyParameters = parameters.filter(hasCacheKey)

        val serializationTypeName = toSerializationRes(resolver) ?: memberSerializationType
        val persistenceTypeName = toPersistenceRes(resolver)
        if (persistenceTypeName != null && serializationTypeName == null) {
            throw IllegalStateException("If @Persistence is used, the current method or parent node needs to have @Serialization.")
        }
        return FunSpec.builder(simpleName.asString())
            .addModifiers(modifiers)
            .addModifiers(KModifier.OVERRIDE)
            .returns(returnTypeName)
            .addParameters(parameters.mapNotNull { p ->
                val name = p.name?.asString() ?: return@mapNotNull null
                ParameterSpec(name, p.type.toTypeName())
            })
            .apply {
                if (jsonKeyParameters.isNotEmpty()) {
                    val n = Named.produce(names, "v")
                    names.add(Named(n, Named.TYPE_VARIABLE or Named.NAME_BODY))
                    addComment("wrap data that is passed using json.")
                    addStatement("val %N = hashMapOf<%T, %T>()", n, STRING, ANY_NULLABLE)
                    for (parameter in jsonKeyParameters) {
                        val jsonKey = parameter.getAnnotation(JsonKey::class) ?: continue
                        val k = jsonKey.value
                        val v = parameter.name?.asString() ?: continue
                        addStatement("%N[%S] = %N", n, k, v)
                    }
                }
                // 持久化条件
                val persistenceCondition = persistenceTypeName != null
                if (persistenceCondition) {
                    val n = Named.produce(names, "v")
                    names.add(Named(n, Named.TYPE_TEMPORARY or Named.NAME_CACHE_KEYS))
                    addStatement("val %N = %T()", n, CACHE_KEYS_CLASS_NAME)
                    for (parameter in cacheKeyParameters) {
                        val k = parameter.getAnnotation(CacheKey::class)?.value ?: continue
                        val v = parameter.name?.asString() ?: continue
                        addStatement("%N[%S] = %N", n, k, v)
                    }
                }
                val appendCondition = appends.isNotEmpty()
                if (appendCondition || persistenceCondition) {
                    val s = pathParameters.fold(pathRes.value) { v1, v2 ->
                        val k = v2.getAnnotation(Path::class)?.value
                        val v = v2.name?.asString()
                        if (k == null || v == null) {
                            v1
                        } else {
                            v1.replace("{$k}", "\$$v")
                        }
                    }
                    val n = Named.produce(names, "v")
                    names.add(Named(n, Named.TYPE_TEMPORARY or Named.NAME_PATH))
                    addStatement("val %N = %T(\"%L\")", n, pathRes.typeName, s)
                }
                for (res in appends) {
                    val n = Named.produce(names, "v")
                    names.add(Named(n, Named.TYPE_VARIABLE))
                    val pn = names.find { it.flag intersect Named.NAME_PATH } ?: continue
                    val providerTypeName = res.providerTypeName
                    val value = pn.value
                    val key = res.key
                    addStatement(
                        "val %N = %T${if (providerTypeName.isObject) "" else "()"}.provide(%N,%S)", n, providerTypeName.value, value, key
                    )
                }
                if (persistenceCondition && serializationTypeName != null && persistenceTypeName != null) {
                    // if (returnRawTypeName in COROUTINES) {
                        val dcfn = Named.produce(names, "v")
                        names.add(Named(dcfn, Named.TYPE_TEMPORARY or Named.NAME_CONVERTER_DEFAULT_VALUE_FUNC))
                        beginControlFlow("val %N = ",dcfn)
                        addStatement("%T<%T>(%M<%T>())", serializationTypeName.value, returnApiTypeName, TYPE_OF_NAME, returnApiTypeName)
                        endControlFlow()

                        val cn = Named.produce(names, "v")
                        names.add(Named(cn, Named.TYPE_TEMPORARY or Named.NAME_CONVERTER))
                        val vn = if (names.any { it.value == CONVERTERS_NAME }) "this.${CONVERTERS_NAME}" else CONVERTERS_NAME
                        addStatement( "val %N = $vn.getOrPut(%S,%N) as %T<%T>", cn, apiFunc,dcfn, serializationTypeName.value, returnApiTypeName)
                        val pn = Named.produce(names, "v")
                        names.add(Named(pn, Named.TYPE_TEMPORARY or Named.NAME_PERSISTENCE))

                        // TODO: 未使用缓存
                        val condition = persistenceTypeName.persistence.classKind == ClassKind.OBJECT
                        addStatement("val %N = %T${if (condition) "" else "()"}", pn, persistenceTypeName.persistence.value)
                    // }
                }
            }
            .addCode(buildCodeBlock {
                val args = buildString {
                    val values = names.filter {
                        it.flag intersect Named.TYPE_PARAMETER || it.flag intersect Named.TYPE_VARIABLE
                    }
                    for (index in values.indices) {
                        val named = values[index]
                        append("v${index + 1} = ${named.value}, ")
                        if (index < values.size - 1) {
                            append(WRAP)
                        }
                    }
                }
                val resultName = Named.produce(names, "v")
                // 获取response
                val responseCodeBlock = buildCodeBlock {
                    names.add(Named(resultName, Named.TYPE_TEMPORARY))
                    val vn = if (names.any { it.value == API_NAME }) "this@${classDeclaration.implName}.${API_NAME}" else API_NAME
                    addStatement("val %N : %T = ${vn}.%N(%L)", resultName, returnApiTypeName, apiFunc, args)
                }
                when (returnRawTypeName) {
                    FLOW_CLASS_NAME -> {
                        if (persistenceTypeName != null) {
                            val pathName = names.find { it.flag intersect Named.NAME_PATH }?.value
                            val cacheKeysName = names.find { it.flag intersect Named.NAME_CACHE_KEYS }?.value
                            val converterName = names.find { it.flag intersect Named.NAME_CONVERTER }?.value
                            val persistenceName = names.find { it.flag intersect Named.NAME_PERSISTENCE }?.value
                            val en = Named.produce(names, "v")
                            names.add(Named(en, Named.TYPE_TEMPORARY))
                            add(buildCodeBlock {
                                beginControlFlow("val %N: suspend () -> %T = ", en, returnApiTypeName)
                                val vn = if (names.any { it.value == API_NAME }) "this.${API_NAME}" else API_NAME
                                addStatement("${vn}.%N(%L)", apiFunc, args)
                                endControlFlow()
                            })
                            val dispatchTypeName = persistenceTypeName.dispatcher.value
                            val condition = persistenceTypeName.dispatcher.classKind == ClassKind.OBJECT
                            addStatement(
                                "return %T${if (condition) "" else "()"}.invoke(%N, %N, %N, execute = %N, source = %N::source, sink = %N::sink)",
                                dispatchTypeName,
                                pathName,
                                cacheKeysName,
                                converterName,
                                en,
                                persistenceName,
                                persistenceName
                            )
                        } else {
                            beginControlFlow("return %M", FLOW_FUNCTION)
                            add(responseCodeBlock)
                            addStatement("emit(%N)", resultName)
                            endControlFlow()
                        }
                    }

                    DEFERRED_CLASS_NAME -> {
                        val deferredName = Named.produce(names, "v")
                        names.add(Named(deferredName, Named.TYPE_TEMPORARY))
                        addStatement("val %N = %T<%T>()", deferredName, COMPLETABLE_DEFERRED_CLASS_NAME, returnApiTypeName)
                        beginControlFlow("onCreator.coroutineScope.%M", LAUNCH_FLOW_FUNCTION)
                        add(responseCodeBlock)
                        addStatement("%N.complete(%N)", deferredName, resultName)

                        if (persistenceTypeName != null) {
                            val pathName = names.find { it.flag intersect Named.NAME_PATH }?.value
                            val cacheKeysName = names.find { it.flag intersect Named.NAME_CACHE_KEYS }?.value
                            val converterName = names.find { it.flag intersect Named.NAME_CONVERTER }?.value
                            val persistenceName = names.find { it.flag intersect Named.NAME_PERSISTENCE }?.value
                            if (persistenceName != null && pathName != null && cacheKeysName != null && converterName != null) {
                                addStatement("%N.sink(%N,%N,%N,%N.serializer)", persistenceName, resultName, pathName, cacheKeysName, converterName)
                            }
                        }
                        endControlFlow()
                        addStatement("return %N", deferredName)
                    }

                    else -> {
                        add(responseCodeBlock)

                        if (persistenceTypeName != null) {
                            val pathName = names.find { it.flag intersect Named.NAME_PATH }?.value
                            val cacheKeysName = names.find { it.flag intersect Named.NAME_CACHE_KEYS }?.value
                            val converterName = names.find { it.flag intersect Named.NAME_CONVERTER }?.value
                            val persistenceName = names.find { it.flag intersect Named.NAME_PERSISTENCE }?.value
                            if (persistenceName != null && pathName != null && cacheKeysName != null && converterName != null) {
                                beginControlFlow("onCreator.coroutineScope.%M", LAUNCH_FLOW_FUNCTION)
                                addStatement("%N.sink(%N,%N,%N,%N.serializer)", persistenceName, resultName, pathName, cacheKeysName, converterName)
                                endControlFlow()
                            }
                        }

                        addStatement("return %N", resultName)
                    }
                }
            }).build()
    }

}