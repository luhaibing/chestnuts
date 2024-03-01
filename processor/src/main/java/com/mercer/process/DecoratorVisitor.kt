package com.mercer.process

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.validate
import com.mercer.annotate.http.Decorator
import com.mercer.annotate.http.JsonKey
import com.mercer.process.mode.AppendRes
import com.mercer.process.mode.Named
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

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        super.visitClassDeclaration(classDeclaration, data)
        topAppends = classDeclaration.toAppends()
        generateImplSingleton(classDeclaration)
        generateImplProperties(classDeclaration)
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
    }

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
        super.visitFunctionDeclaration(function, data)

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

        val parameters = function.parameters

        val funcName = function.simpleName.getShortName()
        val name = generateApiFunction(
            parameters, appends, funcName, function, modifiers, returnType,
        )

        val path = function.toPath()
        generateImplFunction(
            path, parameters, funcName, modifiers, returnType, appends, name
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
        }
        private object Holder {
            val INSTANCE = TestKotlinImpl()
        }
        */
        implTypeSpec.addType(
            TypeSpec.companionObjectBuilder()
                .addFunction(
                    FunSpec
                        .builder("invoke")
                        .addModifiers(KModifier.OPERATOR)
                        .returns(classDeclaration.toClassName())
                        .addStatement("return Holder.INSTANCE")
                        .build()
                ).build()
        )
        implTypeSpec.addType(
            TypeSpec.objectBuilder("Holder")
                .addModifiers(KModifier.PRIVATE)
                .addProperty(
                    PropertySpec
                        .builder("INSTANCE", classDeclaration.toClassName())
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
                        AnnotationSpec.builder(annotation)
                            .addMember(memberFormat ?: "", value ?: "").build()
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
        path: String?,
        parameters: List<KSValueParameter>,
        funcName: String,
        modifiers: List<KModifier>,
        returnType: TypeName,
        appends: ArrayList<AppendRes>,
        name: String
    ) {
        val names = arrayListOf<Named>()
        names.addAll(parameters.map {
            val flag = if (it.any(JSON_KEY)) Named.TEMPORARY else Named.PARAMETER
            Named(value = it.name!!.getShortName(), flag = flag)
        })
        val jsonKeys = parameters.filter(HAS_JSON_KEY)

        val funSpecBuilder = FunSpec.builder(funcName)
            .addModifiers(modifiers)
            .addModifiers(KModifier.OVERRIDE)
            .returns(returnType)
            .addParameters(
                parameters.map {
                    val pType: TypeName = it.type.toTypeName()
                    val pName = it.name?.getShortName()!!
                    ParameterSpec.builder(pName, pType).build()
                }
            )

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
        for (res in appends) {
            val named = Named(
                value = Named.produce(names),
                flag = Named.VARIABLE + Named.TEMPORARY + Named.TO_JSON
            )
            names.add(named)
            funSpecBuilder.addStatement(
                "val %N = %T().provide(%S,%S)",
                named.value,
                res.provider,
                path ?: "null",
                res.name ?: ""
            )
        }
        val returnRawType = if (returnType is ParameterizedTypeName) {
            returnType.rawType
        } else {
            returnType
        }

        val args = buildString {
            val values = names.filter {
                it.flag and Named.PARAMETER == Named.PARAMETER
                        || it.flag and Named.VARIABLE == Named.VARIABLE
            }
            for (index in values.indices) {
                val named = values[index]
                if (named.flag and Named.TO_JSON == Named.TO_JSON) {
                    append("v${index + 1} = ${named.value}, ")
                } else {
                    append("v${index + 1} = ${named.value}, ")
                }
                if (index < values.size - 1) {
                    append(WRAP)
                }
            }
        }
        when (returnRawType) {
            FLOW_CLASS_NAME -> {
                funSpecBuilder.addStatement(
                    "return onCreator.suspend2flow{\rapi.%N(%L)\r}",
                    name, args
                )
            }

            DEFERRED_CLASS_NAME -> {
                funSpecBuilder.addStatement(
                    "return onCreator.suspend2deferred{\rapi.%N(%L)\n}",
                    name, args
                )
            }

            else -> {
                funSpecBuilder.addStatement("return api.%N(%L)", name, args)
            }
        }
        implTypeSpec.addFunction(funSpecBuilder.build())
    }

}