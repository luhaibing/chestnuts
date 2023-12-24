package com.mercer.process

import com.google.devtools.ksp.KSTypeNotPresentException
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toClassName
import java.security.MessageDigest
import java.util.Locale

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


fun annotation2Spec(value: KSAnnotation): AnnotationSpec {
    return value.toAnnotationSpec()
}

fun KSAnnotated.toSpecs(predicate: (AnnotationSpec) -> Boolean = { true }): List<AnnotationSpec> {
    return annotations.map(::annotation2Spec).filter(predicate).toList()
}

val RETROFIT: (AnnotationSpec) -> Boolean = {
    it.typeName.toString().startsWith("retrofit2.http")
}

@OptIn(KspExperimental::class)
fun parseToClassName(block: () -> Any): ClassName {
    return try {
        block()
        TODO()
    } catch (e: KSTypeNotPresentException) {
        e.ksType.toClassName()
    }
}