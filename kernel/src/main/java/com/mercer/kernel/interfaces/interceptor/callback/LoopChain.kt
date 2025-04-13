package com.mercer.kernel.interfaces.interceptor.callback


/**
 * @author :Mercer
 * @Created on 2025/04/12.
 * @Description:
 *   责任链循环器
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
open class LoopChain<In>(
    val position: Int = 0,
    val interceptors: List<Interceptor<In>>,
    val onCompletions: List<Interceptor.Chain.Context<In>.(Exception?) -> Unit>
) : Interceptor.Chain<In> where In : Any {

    override lateinit var input: In

    override fun proceed(value: In) {
        input = value
        val next = next()
        val interceptor = interceptors.getOrNull(position)
        interceptor?.intercept(next) ?: interrupt(null)
    }

    override fun interrupt(value: Exception?) {
        val context = Interceptor.Chain.Context(position - 1, interceptors)
        for (onCompletion in onCompletions) {
            onCompletion.invoke(context, value)
        }
    }

    open fun next(): LoopChain<In> {
        return LoopChain(position + 1, interceptors, onCompletions).also {
            it.input = input
        }
    }

}