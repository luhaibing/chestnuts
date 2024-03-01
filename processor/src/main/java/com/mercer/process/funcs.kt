package com.mercer.process

import com.google.devtools.ksp.KSTypeNotPresentException
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.mercer.annotate.http.Append
import com.mercer.annotate.http.JsonKey
import com.mercer.core.Type
import com.mercer.process.mode.AppendRes
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HEAD
import retrofit2.http.HTTP
import retrofit2.http.OPTIONS
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import java.security.MessageDigest
import java.util.Locale

/**
 * author:  mercer
 * date:    2024/2/17 11:57
 * desc:
 *
 */
fun String.md5(): String {
    try {
        val m = MessageDigest.getInstance("MD5")
        m.update(toByteArray(charset("UTF8")))
        val s = m.digest()
        var result = ""
        for (i in s.indices) {
            result += Integer.toHexString(0x000000FF and s[i].toInt() or -0x100).substring(6)
        }
        return result.uppercase(Locale.ROOT)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}

fun KSAnnotated.any(predicate: (TypeName) -> Boolean): Boolean {
    return annotations.any {
        predicate(it.annotationType.toTypeName())
    }
}

fun KSAnnotated.toAnnotationSpecs(predicate: (TypeName) -> Boolean = { true }): List<AnnotationSpec> {
    return annotations.filter {
        predicate(it.annotationType.toTypeName())
    }.map {
        it.toAnnotationSpec()
    }.toList()
}

/**
 * 过滤出 retrofit 注解
 */
val RETROFIT: (TypeName) -> Boolean = {
    it.toString().startsWith(RETROFIT2_HTTP_PACKAGE)
}

/**
 * 过滤出 JsonKey 注解
 */
val JSON_KEY: (TypeName) -> Boolean = {
    it.toString().startsWith(JsonKey::class.java.canonicalName)
}

val HAS_RETROFIT: (KSAnnotated) -> Boolean = {
    it.any(RETROFIT)
}

val HAS_JSON_KEY: (KSAnnotated) -> Boolean = {
    it.any(JSON_KEY)
}

@OptIn(KspExperimental::class)
fun KSAnnotated.toAppends(): List<AppendRes> {
    return getAnnotationsByType(Append::class).map {
        val type = it.value
        val entry = it.entry
        val name = entry.name
        val provider = parseToTypeName { entry.value }
        when (type) {
            Type.HEADER -> {
                AppendRes(
                    name = name,
                    annotation = retrofit2.http.Header::class.asTypeName(),
                    memberFormat = AppendRes.FORMAT_1,
                    provider = provider,
                )
            }

            Type.QUERY -> {
                AppendRes(
                    name = name,
                    annotation = retrofit2.http.Query::class.asTypeName(),
                    memberFormat = AppendRes.FORMAT_1,
                    provider = provider,
                )
            }

            Type.FIELD -> {
                AppendRes(
                    name = name,
                    annotation = retrofit2.http.Field::class.asTypeName(),
                    memberFormat = AppendRes.FORMAT_1,
                    provider = provider,
                )
            }

            Type.PART -> {
                AppendRes(
                    name = name,
                    annotation = retrofit2.http.Part::class.asTypeName(),
                    memberFormat = AppendRes.FORMAT_1,
                    provider = provider,
                )
            }
        }
    }.toList()
}

@OptIn(KspExperimental::class)
fun parseToTypeName(block: () -> Any): ClassName {
    return try {
        block()
        TODO()
    } catch (e: KSTypeNotPresentException) {
        e.ksType.toClassName()
    }
}

@OptIn(KspExperimental::class)
fun KSFunctionDeclaration.toPath(): String? {
    val put = getAnnotationsByType(PUT::class).firstOrNull()
    put != null && return put.value

    val delete = getAnnotationsByType(DELETE::class).firstOrNull()
    delete != null && return delete.value

    val post = getAnnotationsByType(POST::class).firstOrNull()
    post != null && return post.value

    val get = getAnnotationsByType(GET::class).firstOrNull()
    get != null && return get.value

    val head = getAnnotationsByType(HEAD::class).firstOrNull()
    head != null && return head.value

    val patch = getAnnotationsByType(PATCH::class).firstOrNull()
    patch != null && return patch.value

    val options = getAnnotationsByType(OPTIONS::class).firstOrNull()
    options != null && return options.value

    val http = getAnnotationsByType(HTTP::class).firstOrNull()
    http != null && return http.path

    return null
}
