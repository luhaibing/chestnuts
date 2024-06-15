package com.mercer.kernel.throwable.consumers

import com.mercer.kernel.interfaces.ThrowableInterceptor
import com.mercer.kernel.interfaces.interceptor.Interceptor

/**
 * author:  Mercer
 * date:    2024/6/22
 * desc:
 *   最后的异常消费者
 */
class LastThrowableConsumer : ThrowableInterceptor {
    override suspend fun intercept(chain: Interceptor.Chain<Throwable, Throwable?>): Throwable {
        // 回传
        return chain.input
    }
}