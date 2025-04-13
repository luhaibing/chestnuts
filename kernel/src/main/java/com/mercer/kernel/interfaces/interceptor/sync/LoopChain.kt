package com.mercer.kernel.interfaces.interceptor.sync

/**
 * @author :Mercer
 * @Created on 2025/04/12.
 * @Description:
 *   责任链循环器
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
open class LoopChain<In, Out>(
    val position: Int = 0,
    val interceptors: List<Interceptor<In, Out>>,
) : Interceptor.Chain<In, Out> where In : Any, Out : Any? {

    override lateinit var input: In

    override fun proceed(value: In): Out {
        input = value
        val next = next()
        val interceptor = interceptors.getOrNull(position)
        if (interceptor == null) {
            val previous = interceptors[position - 1]
            throw NullPointerException("The interceptor(${previous::class}) is already at the end.")
        }
        return interceptor.intercept(next)
    }

    open fun next(): LoopChain<In, Out> {
        return LoopChain(position + 1, interceptors).also {
            it.input = input
        }
    }

}