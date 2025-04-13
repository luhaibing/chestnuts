package com.mercer.kernel.throwable.flow

import com.mercer.kernel.interfaces.interceptor.coroutines.Interceptor

/**
 * @author :Mercer
 * @Created on 2025/04/13.
 * @Description:
 *   末端异常处理器
 */
object EndThrowableConsumer : ThrowableConsumer {

    override suspend fun intercept(chain: Interceptor.Chain<Throwable, Throwable?>): Throwable? {
        return chain.input
    }

}