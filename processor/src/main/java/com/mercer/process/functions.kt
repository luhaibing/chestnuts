package com.mercer.process

import com.google.devtools.ksp.KSTypeNotPresentException
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSVisitor
import com.mercer.annotate.http.Append
import com.mercer.annotate.http.JsonKey
import com.mercer.core.Flag
import com.mercer.core.Flag.FLAG_FORM
import com.mercer.core.Flag.FLAG_MULTIPART
import com.mercer.core.Flag.FLAG_NONE
import com.mercer.core.Path
import com.mercer.process.mode.AppendRes
import com.mercer.process.mode.PathRes
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import retrofit2.http.DELETE
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import java.security.MessageDigest
import java.util.Locale
import kotlin.reflect.KClass

/**
 * author:  Mercer
 * date:    2024/8/10
 * desc:
 *   函数
 */

/**
 * 字符串 转 MD5
 */
val String.md5: String
    get() {
        return try {
            val m = MessageDigest.getInstance("MD5")
            m.update(toByteArray(charset("UTF8")))
            val s = m.digest()
            var result = ""
            for (i in s.indices) {
                result += Integer.toHexString(0x000000FF and s[i].toInt() or -0x100).substring(6)
            }
            result.uppercase(Locale.ROOT)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

private fun KSTypeReference?.typeName(): String {
    var value = (this?.toTypeName() ?: Unit::class.asTypeName()).toString()
    value = value.replace("kotlin.collections.", "")
    value = value.replace("kotlinx.coroutines.flow.", "")
    value = value.replace("kotlinx.coroutines.", "")
    value = value.replace("kotlin.", "")
    value = value.replace("okhttp3.", "")
    return value
}

/**
 * 方法签名 [方法名(参数类型,...)]
 */
val KSFunctionDeclaration.signature: String
    get() {
        /*
        val parameters = parameters.joinToString(",") { p ->
            // arrayOf(p.type.typeName(), p.name!!.asString()).joinToString(":")
            p.type.typeName()
        }
        return "${qualifiedName!!.asString()}(${parameters}):${returnType.typeName()}"
        */
        val parameters = parameters.joinToString(",") { it.type.typeName() }
        return "${qualifiedName!!.asString()}(${parameters})"
    }

/**
 * 获取节点的单个注解
 */
@OptIn(KspExperimental::class)
fun <T : Annotation> KSAnnotated.getAnnotation(value: KClass<T>): T? {
    return getAnnotationsByType(value).firstOrNull()
}

/**
 * 获取节点的某种注解
 */
@Suppress("unused")
@OptIn(KspExperimental::class)
fun <T : Annotation> KSAnnotated.getAnnotations(value: KClass<T>): Sequence<T> {
    return getAnnotationsByType(value)
}

/**
 * 获取注解中的类型的值
 */
@OptIn(KspExperimental::class)
fun <T : Annotation> T.toTypeName(block: T.() -> Any): ClassName {
    return try {
        block()
        throw Exception()
    } catch (e: KSTypeNotPresentException) {
        e.ksType.toClassName()
    }
}

/**
 * 是否相交(位运算)
 */
infix fun Int.intersect(value: Int): Boolean {
    return this and value == value
}

/**
 * 是否为目标的子类
 * @receiver     节点
 * @param target 类型
 */
fun KSClassDeclaration.isSubClassOf(target: ClassName): Boolean {
    for (superType in superTypes) {
        val ksType = superType.resolve()
        val toClassName = ksType.toClassName()
        return when (toClassName) {
            target -> return true
            ANY -> false
            else -> (ksType.declaration as KSClassDeclaration).isSubClassOf(target)
        }
    }
    return false
}

/**
 * 获取超类的类型参数
 * @receiver        节点
 * @param target    类型
 * @param position  位置
 */
fun KSClassDeclaration.getTypeParameterOf(target: ClassName, position: Int = 0): TypeName? {
    for (superType in superTypes) {
        val ksType = superType.resolve()
        val toClassName = ksType.toClassName()
        return when (toClassName) {
            target -> ksType.arguments[position].type?.resolve()?.toTypeName()
            ANY -> null
            else -> (ksType.declaration as KSClassDeclaration).getTypeParameterOf(target, position)
        }
    }
    return null
}

/**
 * 根据类型名称获取类
 */
fun TypeName.toClassDeclaration(resolver: Resolver): KSClassDeclaration? {
    return resolver.getClassDeclarationByName(toString())
}

/**
 * 是否有某个注解
 */
fun KSAnnotated.hasAnnotation(predicate: (TypeName) -> Boolean): Boolean {
    return annotations.any {
        predicate(it.annotationType.toTypeName())
    }
}

/**
 * 是否有 JsonKey 注解
 */
val hasJsonKey: KSAnnotated.() -> Boolean = {
    hasAnnotation {
        it.toString() == JsonKey::class.java.canonicalName
    }
}

/**
 * 是否有 JsonKey 注解
 */
val hasPath: KSAnnotated.() -> Boolean = {
    hasAnnotation {
        it.toString() == retrofit2.http.Path::class.java.canonicalName
    }
}

/**
 * 过滤出 retrofit 注解
 */
val RETROFIT: (TypeName) -> Boolean = {
    it.toString().startsWith(RETROFIT2_HTTP_PACKAGE)
}

/**
 * 是否有 retrofit2.http.* 注解
 */
val hasRetrofit: KSAnnotated.() -> Boolean = {
    hasAnnotation(RETROFIT)
}

/**
 * 节点转注解
 */
fun KSAnnotated.toAnnotationSpecs(predicate: (TypeName) -> Boolean = { true }): List<AnnotationSpec> {
    return annotations.filter {
        predicate(it.annotationType.toTypeName())
    }.map {
        it.toAnnotationSpec()
    }.toList()
}

/**
 * 获取方法的 请求类型
 */
@OptIn(KspExperimental::class)
fun KSFunctionDeclaration.toPathRes(): Sequence<PathRes> {
    val f = arrayOf(
        if (getAnnotation(FormUrlEncoded::class) != null) FLAG_FORM else FLAG_NONE,
        if (getAnnotation(Multipart::class) != null) FLAG_MULTIPART else FLAG_NONE,
    ).sum()
    return getAnnotationsByType(PUT::class).map {
        PathRes(Path.PUT::class, it.value, Flag.FLAG_PUT or f)
    } + getAnnotationsByType(DELETE::class).map {
        PathRes(Path.DELETE::class, it.value, Flag.FLAG_DELETE or f)
    } + getAnnotationsByType(POST::class).map {
        PathRes(Path.POST::class, it.value, Flag.FLAG_POST or f)
    } + getAnnotationsByType(GET::class).map {
        PathRes(Path.GET::class, it.value, Flag.FLAG_GET or f)
    }
}

@OptIn(KspExperimental::class)
fun KSAnnotated.toAppends(resolver: Resolver): Sequence<AppendRes> {
    return getAnnotationsByType(Append::class).map {
        val key = it.key
        val type = it.type
        val annotation = type.annotation
        val providerTypeName = it.toTypeName { value }
        val returnTypeName = providerTypeName.toClassDeclaration(resolver)!!.declarations
            .filterIsInstance<KSFunctionDeclaration>()
            .find { f ->
                f.simpleName.asString() == PROVIDER_PROVIDE_FUNCTION
            }!!.returnType!!.toTypeName()
        AppendRes(
            annotation = annotation,
            key = key,
            providerTypeName = providerTypeName,
            returnTypeName = returnTypeName,
            flags = type.flags.toList()
        )
    }
}

val KSClassDeclaration.apiName: String
    get() {
        return simpleName.asString() + "Service"
    }
val KSClassDeclaration.implName: String
    get() {
        return simpleName.asString() + "Impl"
    }
val KSClassDeclaration.apiTypeName: TypeName
    get() {
        return ClassName.bestGuess(arrayOf(packageName.asString(), apiName).joinToString("."))
    }
val KSClassDeclaration.implTypeName: TypeName
    get() {
        return ClassName.bestGuess(arrayOf(packageName.asString(), implName).joinToString("."))
    }

fun KSNode.accept(visitor: KSVisitor<Unit, Unit>) {
    accept(visitor, Unit)
}
