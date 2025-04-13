package com.mercer.kernel.throwable.flow

import com.mercer.kernel.interfaces.interceptor.coroutines.Interceptor
import com.mercer.kernel.throwable.EventException
import com.mercer.kernel.throwable.get
import com.mercer.kernel.throwable.minusKey
import kotlin.reflect.KClass

/**
 * @author :Mercer
 * @Created on 2025/04/13.
 * @Description:
 *   事件类异常处理器
 */
abstract class EventExceptionConsumer<T : EventException>(
    private val target: KClass<T>,
    private val predicate: (T) -> Boolean
) : ThrowableConsumer {

    override suspend fun intercept(chain: Interceptor.Chain<Throwable, Throwable?>): Throwable? {
        val input = chain.input
        val find = input[target]
        val throwable = if (find != null && predicate(find)) {
            input.minusKey(target)
        } else {
            input
        }
        throwable ?: return null
        return chain.proceed(throwable)
    }

}