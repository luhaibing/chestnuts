package com.mercer.process

import com.mercer.annotate.http.Decorator
import com.mercer.core.Creator
import com.mercer.core.Path
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * author:  Mercer
 * date:    2024/8/10
 * desc:
 *   常量
 */

/**
 * 类名
 */
// 触发类
val DECORATOR_CLASS_NAME = Decorator::class.asClassName()

// 协程 Flow
val FLOW_CLASS_NAME = Flow::class.asClassName()
val MUTABLE_STATE_FLOW_CLASS_NAME = MutableStateFlow::class.asClassName()
val STATE_FLOW_CLASS_NAME = StateFlow::class.asClassName()

// 协程 延迟获取结果
val DEFERRED_CLASS_NAME = Deferred::class.asClassName()

// 创建器
val CREATOR_CLASS_NAME = Creator::class.asClassName()

val PATH_CLASS_NAME = Path::class.asClassName()


// 协程
val COROUTINES = arrayOf(FLOW_CLASS_NAME, DEFERRED_CLASS_NAME)

const val RETROFIT2_HTTP_PACKAGE = "retrofit2.http"
val COMPLETABLE_DEFERRED_CLASS_NAME = CompletableDeferred::class.asClassName()


val STRING_NULLABLE = STRING.copy(nullable = true)

val ANY_NULLABLE = ANY.copy(nullable = true)

// 泛型 T
val VARIABLE_NAME_T = TypeVariableName.invoke("T")

/**
 * 方法名
 */
val FLOW_FUNCTION = MemberName("kotlinx.coroutines.flow", "flow")
val ON_EACH_FUNCTION = MemberName("kotlinx.coroutines.flow", "onEach")
val RUN_BLOCKING_FLOW_FUNCTION = MemberName("kotlinx.coroutines", "runBlocking")
val SUSPEND_2_DEFERRED_FUNCTION = MemberName("com.mercer.core", "suspend2deferred")
val LAUNCH_FUNCTION_NAME = MemberName("kotlinx.coroutines","launch")
val LET_FUNCTION_NAME = MemberName("kotlinx","let")


/**
 * 常量
 */
// 换行
const val WRAP = "\r\n"

val ON_STATE_DEFAULT_VALUE_FUNCTION = "defaultValue"
val PROVIDER_PROVIDE_FUNCTION = "provide"
val ON_STATE_INNER_CURRENT_FLOW = "_currentFlow"
