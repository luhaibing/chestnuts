package com.mercer.process

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.validate
import com.mercer.core.CachePipeline
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.Import
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
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
                .addImport("kotlinx.coroutines.flow","flow")
                .addImport("kotlinx.coroutines.flow","map")
                //.addImport(ClassName("kotlinx.coroutines.flow", "map"))
                .build()
            val ksFile = node.containingFile
            val sources = arrayListOf<KSFile>()
            ksFile?.let { sources.add(it) }
            val dependencies = Dependencies(true, *sources.toTypedArray())
            apiFileSpec.writeTo(codeGenerator, dependencies)
            implFileSpec.writeTo(codeGenerator, dependencies)
        }

//        runCatching {
//            val logger = env.logger
//            val name = "com.mercer.lib.test2.SimpleCachePipeline"
//            val ksClassDeclaration = resolver.getClassDeclarationByName(name)
//            val superTypes = ksClassDeclaration?.superTypes?.map {
//                it.resolve().declaration as KSClassDeclaration
//            }?.map {
//                it.typeParameters.onEach {
//                    logger.warn("typeParameter >>> $it.")
//                }
//            }?.toList()
//            logger.warn("ksClassDeclaration >>> $ksClassDeclaration.")
//            logger.warn("superTypes >>> $superTypes.")
//            logger.warn("ksClassDeclaration.typeParameters >>> ${ksClassDeclaration?.typeParameters}.")
//        }

//        runCatching {
//            val logger = env.logger
//            val name = "com.mercer.lib.test2.SimpleCachePipeline"
//            val ksName = resolver.getKSNameFromString(name)
//
//            val classBDeclaration = resolver.getClassDeclarationByName(ksName)
//            val superTypes = classBDeclaration?.superTypes?.toList() ?: emptyList()
//            // val first = superTypes.first()
//            val superClassType = superTypes.firstOrNull()?.resolve()
//            val typeArguments = superClassType?.arguments ?: emptyList()
//            val genericType = typeArguments.firstOrNull()?.type?.resolve()
//            val className = (genericType?.declaration as? KSClassDeclaration)?.toClassName()
//            logger.warn("ksName                     >>> $ksName")
//            logger.warn("classBDeclaration          >>> $classBDeclaration")
//            logger.warn("superTypes                 >>> $superTypes")
//
//            logger.warn("superClassType             >>> $superClassType")
//            logger.warn("superClassType             >>> ${superClassType!!.toClassName() == CachePipeline::class.asClassName()}")
//            logger.warn("superClassType             >>> ${superClassType!!.toClassName() == ANY}")
//
//            logger.warn("typeArguments              >>> $typeArguments")
//            logger.warn("genericType                >>> $genericType")
//
//            val ksTypeArgumentList = genericType?.arguments?: emptyList()
//            val type = ksTypeArgumentList.first().type
//            logger.warn("className                  >>> $className")
//            logger.warn("ksTypeArgumentList         >>> $ksTypeArgumentList")
//            logger.warn("ksTypeArgumentList.type    >>> $type")
//
//
//            /*
//            val classBDeclaration = resolver.getClassDeclarationByName(ksName)!!
//            val superTypeReference = classBDeclaration.superTypes.toList().first()
//            val superType = superTypeReference.resolve()
//            logger.warn("superTypeReference >>> $superTypeReference")
//            logger.warn("superType          >>> $superType")
//            val cachePipelineClassDeclaration = resolver.getClassDeclarationByName(CachePipeline::class.qualifiedName!!)
//            val asType = cachePipelineClassDeclaration!!.toClassName()
//            logger.warn("asType             >>> ${asType==superType.toClassName()}")
//
//            logger.warn("asType             >>> ${asType==CachePipeline::class.asClassName()}")
//            logger.warn("asType             >>> ${asType}")
//            logger.warn("asType             >>> ${superType.toTypeName()}")
//            logger.warn("asType             >>> ${superType.toClassName()}")
//            logger.warn("asType             >>> ${superTypeReference.toTypeName()}")
//            */
//            logger.warn("")
//        }

        return emptyList()
    }
}