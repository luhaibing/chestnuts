package com.mercer.process

import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo

/**
 * author:  mercer
 * date:    2024/2/15 00:48
 * desc:
 *   静态代理的生成逻辑
 */
class DecoratorSymbolProcessor(
    private val env: SymbolProcessorEnvironment,
    private val codeGenerator: CodeGenerator = env.codeGenerator,
    private val logger: KSPLogger = env.logger,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        // 查找所有 Decorator 注解的节点,
        resolver.getSymbolsWithAnnotation(Core.DECORATOR_CLASS_NAME.toString())
            .filterIsInstance<KSClassDeclaration>()
            .filter {
                it.validate()
            }
            .filter {
                it.classKind == ClassKind.INTERFACE || it.isAbstract()
            }
            .forEach {
                val packageName = it.packageName.asString()
                val implTypSpec = TypeSpec.classBuilder(it.implName)
                val apiTypeSpec = TypeSpec.interfaceBuilder(it.apiName)
                it.accept(DecoratorVisitor(env, resolver, apiTypeSpec, implTypSpec,it))
                val implFileSpec = FileSpec.builder(packageName, it.implName)
                    .addType(implTypSpec.build())
                    .build()
                val apiFileSpec = FileSpec.builder(packageName, it.apiName)
                    .addType(apiTypeSpec.build())
                    .build()
                val sources = it.containingFile?.let { k ->
                    arrayListOf(k)
                } ?: emptyList()
                val dependencies = Dependencies(true, *sources.toTypedArray())
                apiFileSpec.writeTo(codeGenerator, dependencies)
                implFileSpec.writeTo(codeGenerator, dependencies)
            }

        return emptyList()
    }
}