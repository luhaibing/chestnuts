package com.mercer.kernel.throwable.flow

import com.mercer.kernel.interfaces.interceptor.coroutines.Interceptor

/**
 * @author :Mercer
 * @Created on 2025/04/13.
 * @Description:
 *   常规异常转换器
 */
class RegularExceptionConverter(
    private val converter: (Throwable) -> Throwable?
) : ThrowableConsumer {

    override suspend fun intercept(chain: Interceptor.Chain<Throwable, Throwable?>): Throwable? {
        return converter(chain.input)?.let {
            chain.proceed(it)
        }
    }

}