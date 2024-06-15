package com.mercer.kernel.throwable

import com.mercer.kernel.interfaces.ThrowableInterceptor
import com.mercer.kernel.interfaces.interceptor.Interceptor

/**
 * author:  Mercer
 * date:    2024/6/22
 * desc:
 *   特殊异常转换器
 */
object InherentThrowableConverter : ThrowableInterceptor {

    override suspend fun intercept(chain: Interceptor.Chain<Throwable, Throwable?>): Throwable? {
        return when (val input = chain.input) {
            is InherentThrowable -> input()
            else -> input
        }.let {
            chain.proceed(it)
        }
    }

}