package com.mercer.kernel.interfaces.interceptor

import com.mercer.kernel.interfaces.OnNext

/**
 * author:  Mercer
 * date:    2024/6/22
 * desc:
 *   责任链循环器
 */
class LoopChain<In, Out>(
    private val index: Int = 0,
    private val interceptors: List<Interceptor<In, Out>>,
) : Interceptor.Chain<In, Out>, OnNext<Interceptor.Chain<In, Out>> where In : Any {

    companion object {

        suspend fun <In : Any, Out> start(
            input: In,
            interceptors: List<Interceptor<In, Out>>
        ): Out {
            return LoopChain(0, interceptors).proceed(input)
        }

        suspend fun <In : Any, Out> start(
            input: In, realInterceptor: Interceptor<In, Out>,
            interceptors: List<Interceptor<In, Out>>
        ): Out {
            return start(input, arrayListOf<Interceptor<In, Out>>().apply {
                addAll(interceptors)
                add(realInterceptor)
            })
        }

    }

    override lateinit var input: In

    override suspend fun proceed(value: In): Out {
        input = value
        val next = next()
        val interceptor = interceptors[index]
        return interceptor.intercept(next)
    }

    override fun next(): Interceptor.Chain<In, Out> {
        return LoopChain(index + 1, interceptors)
    }

}