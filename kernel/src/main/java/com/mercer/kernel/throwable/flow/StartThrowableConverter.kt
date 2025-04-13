package com.mercer.kernel.throwable.flow

import com.mercer.kernel.interfaces.interceptor.coroutines.Interceptor
import com.mercer.kernel.throwable.CombinedException
import com.mercer.kernel.throwable.InherentThrowable
import com.mercer.kernel.throwable.get

/**
 * @author :Mercer
 * @Created on 2025/04/13.
 * @Description:
 *   首部异常处理器
 */
internal object StartThrowableConverter : ThrowableConsumer {

    override suspend fun intercept(chain: Interceptor.Chain<Throwable, Throwable?>): Throwable? {
        val input = when (val throwable = chain.input) {
            is InherentThrowable -> throwable()
            else -> throwable
        }
        return if (input is CombinedException) {
            chain.handleCombinedException(input)
        } else {
            chain.proceed(input)
        }
    }

    /**
     * 处理 CombinedException 异常
     */
    private suspend fun Interceptor.Chain<Throwable, Throwable?>.handleCombinedException(input: CombinedException): Throwable? {
        val result = arrayListOf<Throwable>()
        for (key in input.keys) {
            val element = input[key] ?: continue
            val throwable = if (element is InherentThrowable) element() else element
            val proceed = proceed(throwable) ?: continue
            result.add(proceed)
        }
        return CombinedException(result)
    }

}