package com.mercer.kernel.interfaces.throwable

import com.mercer.kernel.interfaces.interceptor.Interceptor
import com.mercer.kernel.throwable.EventException
import com.mercer.kernel.throwable.get
import com.mercer.kernel.throwable.minusKey
import kotlin.reflect.KClass

/**
 * @author :Mercer
 * @Created on 2024/06/22.
 * @Description:
 *   事件类异常处理器
 */
abstract class EventExceptionConsumer<T : EventException>(
    private val target: KClass<T>,
    private val action: (T) -> Unit
) : ThrowableConsumer {

    override suspend fun intercept(chain: Interceptor.Chain<Throwable?, Throwable?>): Throwable? {
        val input = chain.input
        input?.get(target)?.let { action(it) }
        return chain.proceed(input?.minusKey(target))
    }

}