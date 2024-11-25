package com.mercer.kernel.interfaces.throwable

import com.mercer.kernel.interfaces.interceptor.Interceptor
import com.mercer.kernel.throwable.PlainTextException
import com.mercer.kernel.throwable.getInstance
import com.mercer.kernel.throwable.minusKey

/**
 * @author :Mercer
 * @Created on 2024/06/22.
 * @Description:
 *   明文类异常的消费者
 */
class PlainTextThrowableConsumer(
    private val predicate: (PlainTextException) -> Unit
) : ThrowableConsumer {

    override suspend fun intercept(chain: Interceptor.Chain<Throwable?, Throwable?>): Throwable? {
        val input = chain.input
        var current: Throwable? = input
        var find: PlainTextException? = null
        while (current?.getInstance(PlainTextException::class)?.also { find = it } != null) {
            find?.let {
                current = current?.minusKey(it::class)
                predicate(it)
            }
        }
        return chain.proceed(current)
    }

}