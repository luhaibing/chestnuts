package com.mercer.kernel.interfaces.throwable

import com.mercer.kernel.interfaces.interceptor.Interceptor
import com.mercer.kernel.throwable.InherentThrowable

/**
 * @author :Mercer
 * @Created on 2024/06/22.
 * @Description:
 *   首部异常处理器
 */
internal object StartThrowableConverter : ThrowableConsumer {
    override suspend fun intercept(chain: Interceptor.Chain<Throwable?, Throwable?>): Throwable? {
        return when (val input = chain.input) {
            is InherentThrowable -> input()
            else -> input
        }.let {
            chain.proceed(it)
        }
    }
}