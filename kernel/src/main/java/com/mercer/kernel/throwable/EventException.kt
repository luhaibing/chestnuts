package com.mercer.kernel.throwable

/**
 * @author :Mercer
 * @Created on 2024/06/22.
 * @Description:
 *   行为/动作/事件类 异常
 *   需要配合拦截器(ThrowableInterceptor)使用
 */
abstract class EventException(reason: Throwable? = null) : AbsException(reason) {
    // abstract suspend fun handle(target: Any): Boolean
}
