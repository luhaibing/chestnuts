package com.mercer.process

import com.mercer.annotate.http.Decorator
import com.mercer.core.Creator
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.asClassName
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow

val DECORATOR_CLASS_NAME = Decorator::class.asClassName()
val FLOW_CLASS_NAME = Flow::class.asClassName()
val DEFERRED_CLASS_NAME = Deferred::class.asClassName()
val CREATOR_CLASS_NAME = Creator::class.asClassName()
val COROUTINES = arrayOf(FLOW_CLASS_NAME, DEFERRED_CLASS_NAME)

const val RETROFIT2_HTTP_PACKAGE = "retrofit2.http"
val STRING_NULLABLE = STRING.copy(nullable = true)

val MAP = Map::class.asClassName().parameterizedBy(
    STRING, STRING_NULLABLE
)

val ANY_NULLABLE = Any::class.asClassName().copy(nullable = true)

const val WRAP = "\r\n"
