package com.mercer.kernel.interfaces.throwable

import com.mercer.kernel.interfaces.interceptor.Interceptor

/**
 * @author :Mercer
 * @Created on 2024/06/22.
 * @Description:
 *   末端异常处理器
 */
internal object EndThrowableConsumer : ThrowableConsumer {
    override suspend fun intercept(chain: Interceptor.Chain<Throwable?, Throwable?>): Throwable? {
        return chain.input
    }
}