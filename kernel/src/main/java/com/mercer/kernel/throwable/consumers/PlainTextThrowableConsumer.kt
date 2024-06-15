package com.mercer.kernel.throwable.consumers

import com.mercer.kernel.interfaces.ThrowableInterceptor
import com.mercer.kernel.interfaces.interceptor.Interceptor
import com.mercer.kernel.throwable.PlainTextException
import com.mercer.kernel.throwable.get
import com.mercer.kernel.throwable.minus

/**
 * author:  Mercer
 * date:    2024/6/22
 * desc:
 *   明文类异常的消费者
 */
class PlainTextThrowableConsumer(
    private val predicate: (PlainTextException) -> Unit
) : ThrowableInterceptor {

    override suspend fun intercept(chain: Interceptor.Chain<Throwable, Throwable?>): Throwable? {
        val input = chain.input
        val exception = input[PlainTextException::class]
        val value = if (exception != null) {
            // 完成消费
            predicate(exception)
            input - exception
        } else {
            input
        }
        return chain.proceed(value)
        /*
        // TODO: 消费掉异常
        if (input is PlainTextException) {
            // 中断传递,完成消费
            predicate(input)
            return null
        }
        // 继续传递
        return chain.proceed(input)
        */
    }

}