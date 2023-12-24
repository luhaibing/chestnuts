package com.mercer.process

import com.mercer.annotate.http.Append
import com.mercer.annotate.http.Decorator
import com.squareup.kotlinpoet.asClassName
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow

val DECORATOR_CLASS_NAME = Decorator::class.asClassName()
val APPEND_CLASS_NAME = Append::class.asClassName()


val FLOW_CLASS_NAME = Flow::class.asClassName()
val DEFERRED_CLASS_NAME = Deferred::class.asClassName()

val COROUTINES = arrayOf(FLOW_CLASS_NAME, /*DEFERRED_CLASS_NAME*/)

// 换行
const val WRAP = "\r\n"