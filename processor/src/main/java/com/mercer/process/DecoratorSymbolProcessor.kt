package com.mercer.process

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
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
    private val env: SymbolProcessorEnvironment
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val codeGenerator = env.codeGenerator
        // 查找所有 Decorator 注解的节点,
        val finds = resolver.getSymbolsWithAnnotation(DECORATOR_CLASS_NAME.toString())
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.validate() }
        finds.map {
            val packageName = it.packageName.asString()
            val apiName = it.simpleName.asString() + "Service"
            val implName = it.simpleName.asString() + "Impl"
            val apiTypeSpec = TypeSpec.interfaceBuilder(apiName)
            val implTypSpec = TypeSpec.classBuilder(implName)
            it.accept(DecoratorVisitor(env, packageName, apiTypeSpec, implTypSpec), Unit)
            it to arrayOf(apiTypeSpec, implTypSpec)
        }.forEach { (node, specs) ->
            val (apiTypeBuilder, implTypeBuilder) = specs
            val packageName = node.packageName.asString()
            val apiType = apiTypeBuilder.build()
            val implTypSpec = implTypeBuilder.build()
            val apiFileSpec = FileSpec.builder(packageName, apiType.name!!)
                .addType(apiTypeBuilder.build())
                .build()
            val implFileSpec = FileSpec.builder(packageName, implTypSpec.name!!)
                .addType(implTypeBuilder.build())
                .build()
            val ksFile = node.containingFile
            val sources = arrayListOf<KSFile>()
            ksFile?.let { sources.add(it) }
            val dependencies = Dependencies(true, *sources.toTypedArray())
            apiFileSpec.writeTo(codeGenerator, dependencies)
            implFileSpec.writeTo(codeGenerator, dependencies)
        }
        return emptyList()
    }
}