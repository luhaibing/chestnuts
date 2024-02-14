package com.mercer.process

import com.mercer.annotate.http.Append
import com.mercer.annotate.http.Decorator
import com.mercer.annotate.http.JsonKey
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.jvm.jvmSuppressWildcards
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow

val DECORATOR_CLASS_NAME = Decorator::class.asClassName()
val APPEND_CLASS_NAME = Append::class.asClassName()


val FLOW_CLASS_NAME = Flow::class.asClassName()
val DEFERRED_CLASS_NAME = Deferred::class.asClassName()

val COROUTINES = arrayOf(FLOW_CLASS_NAME /*DEFERRED_CLASS_NAME*/)

// 换行
const val WRAP = "\r\n"

val RETROFIT2_HTTP = "retrofit2.http"

val JSON_KEY = JsonKey::class.java.canonicalName

val MAP = Map::class.asClassName().parameterizedBy(
    String::class.asClassName(), Any::class.asClassName().copy(nullable = true)
)
