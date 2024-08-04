package com.mercer.process

import com.mercer.annotate.http.Decorator
import com.mercer.core.CachePipeline
import com.mercer.core.Creator
import com.mercer.core.OnShared
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow

val DECORATOR_CLASS_NAME = Decorator::class.asClassName()
val FLOW_CLASS_NAME = Flow::class.asClassName()
val DEFERRED_CLASS_NAME = Deferred::class.asClassName()
val CREATOR_CLASS_NAME = Creator::class.asClassName()
val CACHE_PIPELINE_CLASS_NAME = CachePipeline::class.asClassName()
val ON_SHARED_CLASS_NAME = OnShared::class.asClassName()

val COROUTINES = arrayOf(FLOW_CLASS_NAME, DEFERRED_CLASS_NAME)

const val RETROFIT2_HTTP_PACKAGE = "retrofit2.http"
val STRING_NULLABLE = STRING.copy(nullable = true)

val ANY_NULLABLE = Any::class.asClassName().copy(nullable = true)

val FLOW_FUNCTION = MemberName("kotlinx.coroutines.flow", "flow")
val RUN_BLOCKING_FLOW_FUNCTION = MemberName("kotlinx.coroutines", "runBlocking")
val SUSPEND_2_DEFERRED_FUNCTION = MemberName("com.mercer.core", "suspend2deferred")

val VARIABLE_NAME_T = TypeVariableName.invoke("T")

const val WRAP = "\r\n"

val COMPLETABLE_DEFERRED_CLASS_NAME = CompletableDeferred::class.asClassName()


const val FLAG_NONE = 1
const val FLAG_OVERRIDE = FLAG_NONE shl 1

