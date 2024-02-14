package com.mercer.process

import com.google.devtools.ksp.KSTypeNotPresentException
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.validate
import com.mercer.annotate.http.Decorator
import com.mercer.annotate.http.JsonKey
import com.mercer.core.Argument
import com.mercer.process.AppendDesc.Companion.toAppends
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toKModifier
import com.squareup.kotlinpoet.ksp.toTypeName
import retrofit2.http.Body
import retrofit2.http.Headers

class DecoratorVisitor(
    @Suppress("unused")
    private val logger: KSPLogger,
    private val impl: TypeSpec.Builder,
    private val api: TypeSpec.Builder,
    private val appends: List<AppendDesc>,
    private val apiClassName: ClassName,
    private val implClassName: ClassName,
) : KSVisitorVoid() {

    @OptIn(KspExperimental::class)
    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        super.visitClassDeclaration(classDeclaration, data)

        impl.addSuperinterface(classDeclaration.toClassName())
        impl.addFunction(
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
        impl.addType(
            TypeSpec.companionObjectBuilder()
                .addFunction(
                    FunSpec.builder("invoke")
                        .addModifiers(KModifier.OPERATOR)
                        .returns(classDeclaration.toClassName())
                        .addStatement("return Holder.INSTANCE")
                        .build()
                )
                .build()
        )
        impl.addType(
            TypeSpec.objectBuilder("Holder")
                .addModifiers(KModifier.PRIVATE)
                .addProperty(
                    PropertySpec.builder("INSTANCE", classDeclaration.toClassName())
                        .initializer("%T()", implClassName)
                        .build()
                )
                .build()
        )

        val decoratorByType = classDeclaration.getAnnotationsByType(Decorator::class)
        val onCreatorTypeName = try {
            decoratorByType.first().value
            TODO()
        } catch (e: KSTypeNotPresentException) {
            e.ksType.toTypeName()
        }

        /*
        private val onCreator by lazy {
                SimpleCreator()
        }
        */
        PropertySpec.builder("onCreator", onCreatorTypeName)
            .addModifiers(KModifier.PRIVATE)
            .delegate("lazy {%T()}", onCreatorTypeName)
            .build()
            .let {
                impl.addProperty(it)
            }

        /*
        private val api by lazy {
            onCreator.create(TestKotlinApi::class)
        }
        */
        PropertySpec.builder("api", apiClassName)
            .addModifiers(KModifier.PRIVATE)
            .delegate("lazy { onCreator.create(%T::class)}", apiClassName)
            .build()
            .let {
                impl.addProperty(it)
            }

        /*
        fun Any.toJson(): String? {
            return onCreator.any2str(this)
        }
        */
        FunSpec.builder("toJson")
            .addModifiers(KModifier.PRIVATE)
            .receiver(Any::class.asTypeName().copy(nullable = true))
            .returns(String::class.asTypeName().copy(nullable = true))
            .addStatement("return onCreator.any2str(this)")
            .build()
            .let {
                impl.addFunction(it)
            }
        classDeclaration.getDeclaredFunctions()
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

    @OptIn(KspExperimental::class)
    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
        super.visitFunctionDeclaration(function, data)
        val name = function.simpleName.getShortName()
        val returnType = function.returnType?.toTypeName() ?: Unit::class.asClassName()
        val isCoroutines = returnType is ParameterizedTypeName && returnType.rawType in COROUTINES

        val modifiers = function.modifiers
            .mapNotNull { it.toKModifier() }
            .toMutableList()

        val parameters = function.parameters.filter { p ->
            p.annotations.any {
                it.annotationType.toTypeName().toString().startsWith(RETROFIT2_HTTP)
            }
        }.map {
            val pType: TypeName = it.type.toTypeName()
            val pName = it.name?.getShortName()!!
            ParameterSpec.builder(pName, pType)
                .addAnnotations(it.toSpecs(RETROFIT))
                .build()
        }.toMutableList()
        val hasJsonKey: Boolean = function.parameters.map {
            it.annotations.toList()
        }.flatten().any {
            it.annotationType.toTypeName().toString() == JSON_KEY
        }
        if (hasJsonKey) {
            val pns = parameters.map { it.name }
            val bn = produceName(pns)
            parameters.add(ParameterSpec.builder(bn, MAP).addAnnotation(Body::class).build())
        }

        val allParameters: MutableList<ParameterSpec> = arrayListOf()
        allParameters.addAll(parameters)

        allParameters.addAll(appends2parameters(appends))
        val toAppends = function.toAppends()
        allParameters.addAll(appends2parameters(toAppends))

        val parameterSpecs = allParameters.mapIndexed { index, spec ->
            spec.toBuilder("v${index + 1}").build()
        }
        FunSpec.builder(name)
            .addAnnotations(function.toSpecs(RETROFIT))
            .addModifiers(modifiers)
            .addModifiers(KModifier.ABSTRACT)
            .addParameters(parameterSpecs)
            .apply {
                if (isCoroutines) {
                    addModifiers(KModifier.SUSPEND)
                    returns((returnType as ParameterizedTypeName).typeArguments.first())
                } else {
                    returns(returnType)
                }
            }
            .let {
                api.addFunction(it.build())
            }

        ///////////////////////////////////////////

        FunSpec.builder(name)
            .addModifiers(modifiers.toMutableList().apply {
                remove(KModifier.ABSTRACT)
                add(KModifier.OVERRIDE)
            })
            .returns(returnType)
            .addParameters(
                function.parameters.map {
                    val pType: TypeName = it.type.toTypeName()
                    val pName = it.name?.getShortName()!!
                    ParameterSpec.builder(pName, pType).build()
                }
            )
            .also {

                val allParameterNames = arrayListOf<String>()
                // 有效
                val validParameterNames = arrayListOf<String>()
                val providerNames = arrayListOf<String>()
                val ps = function.parameters
                for (p in ps) {
                    val pn = p.name!!.asString()
                    allParameterNames.add(pn)
                    if (p.annotations.any { a ->
                            a.annotationType.toTypeName().toString().startsWith(RETROFIT2_HTTP)
                        }) {
                        validParameterNames.add(pn)
                    }
                }

                if (hasJsonKey) {
                    val bodyParameterName = produceName(allParameterNames)
                    validParameterNames.add(bodyParameterName)
                    allParameterNames.add(bodyParameterName)
                    it.addStatement("val %N = hashMapOf<String,Any?>()", bodyParameterName)
                    function.parameters.filter { p ->
                        p.annotations.any { a ->
                            a.annotationType.toTypeName().toString() == JSON_KEY
                        }
                    }.map { p ->
                        val k = p.getAnnotationsByType(JsonKey::class).first().value
                        val v = p.name!!.asString()
                        // it.addStatement("%N.put(%S,%N)", bodyParameterName, k,v)
                        it.addStatement("%N[%S]=%N", bodyParameterName, k, v)
                    }
                }

                val allAppends = arrayListOf<AppendDesc>().apply {
                    addAll(appends)
                    addAll(toAppends)
                }
                @Suppress("KotlinConstantConditions")
                if (allAppends.isNotEmpty()) {
                    val argNames = arrayListOf<String>()
                    for (i in ps.indices) {
                        val p = ps[i]
                        p.addRetrofitArgument(it, allParameterNames, argNames)
                    }
                    val finds = function.getAnnotationsByType(Headers::class).toList()
                    if (finds.isNotEmpty()) {
                        val headers = finds.first()
                        val values = headers.value
                        for (value in values) {
                            val n = produceName(allParameterNames + argNames)
                            argNames.add(n)
                            val indexOf = value.indexOf(":")
                            val k = value.substring(0, indexOf).trim()
                            val v = value.substring(indexOf + 1).trim()
                            it.addStatement("val %N = %T(%S, %S)", n, Argument.Header::class, k, v)
                        }
                    }
                    if (hasJsonKey) {
                        val n = produceName(allParameterNames + argNames)
                        argNames.add(n)
                        val bn = validParameterNames.last()
                        it.addStatement("val %N = %T(%N)", n, Argument.Body::class, bn)
                    }

                    val providerArgNames = arrayListOf<String>()
                    for (index in allAppends.indices) {
                        val append = allAppends[index]
                        val excludes =
                            allParameterNames + argNames + providerNames + providerArgNames
                        val n1 = produceName(excludes)
                        providerNames.add(n1)
                        it.addStatement(
                            "val %N = %T().provide(%L)",
                            n1,
                            append.providerTypeName,
                            argNames.joinToString(",")
                        )
                        if (index != allAppends.size - 1) {
                            val n2 = produceName(excludes + n1)
                            providerArgNames.add(n2)
                            it.addCode(append.statement(n2, n1))
                            argNames.add(n2)
                        }
                    }
                }

                val args = StringBuilder().apply {
                    var offset = 1
                    for (n in validParameterNames) {
                        append("v$offset = $n,")
                        offset += 1
                    }
                    @Suppress("KotlinConstantConditions")
                    for (n in providerNames) {
                        append("v$offset = $n.toJson(),")
                        offset += 1
                    }
                }.toString()
                if (isCoroutines) {
                    it.addStatement("return onCreator.suspend2flow {\r api.%N(%L)}", name, args)
                } else {
                    it.addStatement("return api.%N(%L)", name, args)
                }

            }
            .let {
                impl.addFunction(it.build())
            }
    }

}

fun produceName(excludes: List<String>): String {
    var position = 1
    while (true) {
        val name = "v$position"
        position += 1
        if (name in excludes) {
            continue
        }
        return name
    }
}

@OptIn(KspExperimental::class)
private fun KSValueParameter.addRetrofitArgument(
    builder: FunSpec.Builder,
    names: MutableList<String>,
    pNames: MutableList<String>
) {

    fun addArgumentWithKeyStatement(typeName: TypeName, key: String) {
        val n = produceName(names + pNames)
        pNames.add(n)
        val varName = name!!.asString()
        builder.addStatement("val %N = %T(%S, %N)", n, typeName, key, varName)
    }

    fun addArgumentStatement(typeName: TypeName) {
        val n = produceName(names + pNames)
        pNames.add(n)
        val varName = name!!.asString()
        builder.addStatement("val %N = %T(%N)", n, typeName, varName)
    }

    val queries = getAnnotationsByType(retrofit2.http.Query::class).toList()
    if (queries.isNotEmpty()) {
        val value = queries.first()
        addArgumentWithKeyStatement(Argument.Query::class.asClassName(), value.value)
    }
    val queryMaps = getAnnotationsByType(retrofit2.http.QueryMap::class).toList()
    if (queryMaps.isNotEmpty()) {
        addArgumentStatement(Argument.QueryMap::class.asClassName())
    }
    val fields = getAnnotationsByType(retrofit2.http.Field::class).toList()
    if (fields.isNotEmpty()) {
        val value = fields.first()
        addArgumentWithKeyStatement(Argument.Field::class.asClassName(), value.value)
    }
    val fieldMaps = getAnnotationsByType(retrofit2.http.FieldMap::class).toList()
    if (fieldMaps.isNotEmpty()) {
        addArgumentStatement(Argument.FieldMap::class.asClassName())
    }
    val parts = getAnnotationsByType(retrofit2.http.Part::class).toList()
    if (parts.isNotEmpty()) {
        val value = parts.first()
        addArgumentWithKeyStatement(Argument.Part::class.asClassName(), value.value)
    }
    val partMaps = getAnnotationsByType(retrofit2.http.PartMap::class).toList()
    if (partMaps.isNotEmpty()) {
        addArgumentStatement(Argument.PartMap::class.asClassName())
    }
    val headers = getAnnotationsByType(retrofit2.http.Header::class).toList()
    if (headers.isNotEmpty()) {
        val value = headers.first()
        addArgumentWithKeyStatement(Argument.Header::class.asClassName(), value.value)
    }
    val headerMaps = getAnnotationsByType(retrofit2.http.HeaderMap::class).toList()
    if (headerMaps.isNotEmpty()) {
        addArgumentStatement(Argument.HeaderMap::class.asClassName())
    }
    val bodies = getAnnotationsByType(retrofit2.http.Body::class).toList()
    if (bodies.isNotEmpty()) {
        addArgumentStatement(Argument.Body::class.asClassName())
    }
}