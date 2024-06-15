@file:Suppress("unused")

package com.mercer.kernel.throwable

/**
 * author:  Mercer
 * date:    2024/6/22
 * desc:
 *   内部异常
 *   项目内自定义异常的超类以及基础的几个异常
 */
sealed class AbsException(cause: Throwable?) : RuntimeException(cause)

////////////////////////// 直接子类
/**
 *   明文类异常
 *   可以直接提示给用户
 */
open class PlainTextException(
    val value: String,
    reason: Throwable? = null,
) : AbsException(reason)

/**
 *   中断/暂停 的异常
 */
open class CancelException(cause: Throwable? = null) : AbsException(cause)


/**
 *   行为/动作/事件类 异常
 *   需要配合拦截器(ThrowableInterceptor)使用
 */
abstract class EventException(
    reason: Throwable? = null,
) : AbsException(reason) {
    // abstract suspend fun handle(target: Any): Boolean
}

////////////////////////// 特殊情况

interface InherentThrowable {
    operator fun invoke(): AbsException
}