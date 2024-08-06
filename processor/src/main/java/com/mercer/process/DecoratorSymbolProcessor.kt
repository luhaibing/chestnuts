package com.mercer.process

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Variance
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName
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
            it.accept(DecoratorVisitor(env,resolver, packageName, apiTypeSpec, implTypSpec), Unit)
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

//
//        val zlass1 = resolver.getClassDeclarationByName("com.mercer.lib.test2.SimpleCachePipeline")
//        val zlass2 = resolver.getClassDeclarationByName("com.mercer.core.CachePipeline")
//        env.logger.warn("zlass1 >>> $zlass1")
//        env.logger.warn("zlass2 >>> $zlass2")
//
//        val asType1 = zlass1!!.asType(emptyList())
//        val asType2 = zlass2!!.asType(emptyList())
//
//        env.logger.warn("asType1.isAssignableFrom(asType2) >>> ${asType1.isAssignableFrom(asType2)}")
//        env.logger.warn("asType2.isAssignableFrom(asType1) >>> ${asType2.isAssignableFrom(asType1)}")
//
//
//        env.logger.warn("asType1 >>> $asType1")
//        env.logger.warn("asType2 >>> $asType2")
//
//        val v3 = zlass1!!.toClassName()
//        val v4 = zlass2!!.toClassName()

//        env.logger.warn("v3.isAssignableFrom(v4) >>> ${v3.isAssignableFrom(v4)}")
//        env.logger.warn("v4.isAssignableFrom(v3) >>> ${v4.isAssignableFrom(v3)}")

//        val v5 =Class.forName("com.mercer.lib.test2.SimpleCachePipeline")
//        val v6 =Class.forName("com.mercer.core.CachePipeline")
//
//        env.logger.warn("v5 >>> $v5")
//        env.logger.warn("v6 >>> $v6")

        val v1 = resolver.getClassDeclarationByName("com.mercer.lib.test2.A")
        val v2 = resolver.getClassDeclarationByName("com.mercer.lib.test2.B")


        val ksType: KSType = resolver.getClassDeclarationByName("kotlin.String")!!.asType(emptyList()) // 假设你已经有一个 KSType 实例
        val ksTypeReference: KSTypeReference = resolver.createKSTypeReferenceFromKSType(ksType)
        val ksTypeArgument: KSTypeArgument = resolver.getTypeArgument(
            ksTypeReference,
            Variance.INVARIANT // 你可以根据需要选择合适的 Variance
        )

        val v3 = v1!!.asType(arrayListOf(ksTypeArgument))
        val v4 = v2!!.asType(emptyList())

        env.logger.warn("v3.isAssignableFrom(v4) >>> ${v3.isAssignableFrom(v4)}")
        env.logger.warn("v4.isAssignableFrom(v3) >>> ${v4.isAssignableFrom(v3)}")


        return emptyList()
    }
}