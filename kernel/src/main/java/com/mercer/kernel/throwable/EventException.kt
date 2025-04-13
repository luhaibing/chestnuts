package com.mercer.kernel.throwable

/**
 * @author :Mercer
 * @Created on 2025/04/11.
 * @Description:
 *   行为/动作/事件类 异常
 *   需要配合拦截器(ThrowableInterceptor)使用
 */
abstract class EventException(reason: Throwable? = null) : AbsException(reason)