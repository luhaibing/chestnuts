package com.mercer.process

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.mercer.annotate.http.Append
import com.mercer.core.Argument
import com.mercer.core.Type
import com.mercer.process.AppendDesc.Companion.toAppends
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.writeTo
import kotlin.reflect.KClass


class DecoratorKspProcessor : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return DecoratorSymbolProcessor(environment)
    }
}

private class DecoratorSymbolProcessor(
    val env: SymbolProcessorEnvironment
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val logger = env.logger
        val codeGenerator = env.codeGenerator
        resolver
            .getSymbolsWithAnnotation(DECORATOR_CLASS_NAME.toString())
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.validate() }
            .forEach {
                val appends = it.toAppends()
                val packageName = it.packageName.asString()
                val apiName = it.simpleName.asString() + "Api"
                val implName = it.simpleName.asString() + "Impl"
                val apiTypeBuilder = TypeSpec.interfaceBuilder(apiName)
                val implTypeBuilder = TypeSpec.classBuilder(implName)
                val apiClassName = ClassName.bestGuess("$packageName.$apiName")
                val implClassName = ClassName.bestGuess("$packageName.$implName")
                it.accept(
                    DecoratorVisitor(
                        logger,
                        implTypeBuilder,
                        apiTypeBuilder,
                        appends,
                        apiClassName,
                        implClassName,
                    ), Unit
                )
                val apiFileSpec = FileSpec.builder(packageName, apiName)
                    .addType(apiTypeBuilder.build())
                    .build()
                val implFileSpec = FileSpec.builder(packageName, implName)
                    .addType(implTypeBuilder.build())
                    .build()
                val dependencies = Dependencies(false)
                apiFileSpec.writeTo(codeGenerator, dependencies)
                implFileSpec.writeTo(codeGenerator, dependencies)
            }

        return emptyList()
    }

}

typealias StatementBlock = (String, TypeName, String?, String) -> CodeBlock

data class AppendDesc(
    val value: String?,
    val providerTypeName: ClassName,
    val annotationTypeName: ClassName,
    val dataTypeName: ClassName,
    val memberFormat: String?,
    val statementBlock: StatementBlock,
) {

    fun statement(v1: String, v2: String): CodeBlock {
        return statementBlock(v1, dataTypeName, value, v2)
    }

    companion object {
        private val WITH_KEY_CODE_BLOCK: StatementBlock = { v1, v2, v3, v4 ->
            CodeBlock.builder().addStatement("val %N = %T(%S, %N)", v1, v2, v3!!, v4).build()
        }
        private val WITHOUT_KEY_CODE_BLOCK: StatementBlock = { v1, v2, _, v3 ->
            CodeBlock.builder().addStatement("val %N = %T(%N)", v1, v2, v3).build()
        }

        @OptIn(KspExperimental::class)
        fun KSAnnotated.toAppends(): List<AppendDesc> {
            return getAnnotationsByType(Append::class).map {
                val type = it.value
                val entry = it.entry
                val name = entry.name
                val providerTypeName = parseToClassName { entry.value }
                val annotationTypeName: KClass<*>
                val dataTypeName: KClass<*>
                val memberFormat: String?
                val statement: StatementBlock
                when (type) {
                    Type.HEADER -> {
                        annotationTypeName = retrofit2.http.Header::class
                        dataTypeName = Argument.Header::class
                        memberFormat = "value = %S"
                        statement = WITH_KEY_CODE_BLOCK
                    }

                    Type.QUERY -> {
                        annotationTypeName = retrofit2.http.Query::class
                        dataTypeName = Argument.Query::class
                        memberFormat = "value = %S"
                        statement = WITH_KEY_CODE_BLOCK
                    }

                    Type.FIELD -> {
                        annotationTypeName = retrofit2.http.Field::class
                        dataTypeName = Argument.Field::class
                        memberFormat = "value = %S"
                        statement = WITH_KEY_CODE_BLOCK
                    }

                    Type.PART -> {
                        annotationTypeName = retrofit2.http.Part::class
                        dataTypeName = Argument.Part::class
                        memberFormat = "value = %S"
                        statement = WITH_KEY_CODE_BLOCK
                    }

                    Type.BODY -> {
                        annotationTypeName = retrofit2.http.Body::class
                        dataTypeName = Argument.Body::class
                        memberFormat = null
                        statement = WITHOUT_KEY_CODE_BLOCK
                    }
                }
                AppendDesc(
                    value = name,
                    providerTypeName = providerTypeName,
                    annotationTypeName = annotationTypeName.asClassName(),
                    dataTypeName = dataTypeName.asClassName(),
                    memberFormat = memberFormat,
                    statementBlock = statement
                )
            }.toList()
        }
    }

}


fun appends2parameters(values: List<AppendDesc>): List<ParameterSpec> {
    return values.map {
        val builder = ParameterSpec.builder(
            it.value ?: "xxx", String::class.asClassName().copy(nullable = true)
        )
        val annotationSpec = AnnotationSpec
            .builder(
                // ClassName.bestGuess(it.type.annotationTypeName)
                it.annotationTypeName
            )
            .addMember(it.memberFormat ?: "", it.value ?: "")
            .build()
        builder.addAnnotation(annotationSpec)
        builder.build()
    }
}