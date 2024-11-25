package com.mercer.kernel.interfaces.interceptor

import com.mercer.kernel.interfaces.OnNext

/**
 * @author :Mercer
 * @Created on 2024/06/22.
 * @Description:
 *   责任链循环器
 */
open class LoopChain<In, Out>(
    private val position: Int = 0,
    private val values: List<Interceptor<In, Out>>,
) : Interceptor.Chain<In, Out>, OnNext<LoopChain<In, Out>> where In : Any?, Out : Any? {

    override var input: In? = null

    override suspend fun proceed(value: In): Out {
        input = value
        val next = next()
        val interceptor = values[position]
        return interceptor.intercept(next)
    }

    override fun next(): LoopChain<In, Out> {
        return LoopChain(position + 1, values).also {
            it.input = input
        }
    }

    val current: Int
        get() {
            return position
        }

    val interceptors: List<Interceptor<In, Out>>
        get() {
            return values.toList()
        }

}
/*
class IntLoopChain(
    index: Int = 0,
    interceptors: List<Interceptor<Int?, Int?>>
) : LoopChain<Int?, Int?>(index, interceptors) {

    override suspend fun proceed(value: Int?): Int? {
        if (value == null) {
            return null
        }
        return super.proceed(value)
    }

    override fun next(): LoopChain<Int?, Int?> {
        return IntLoopChain(current + 1, interceptors).also {
            it.input = input
        }
    }
}


fun main() = runBlocking {
    val interceptors = arrayListOf<Interceptor<Int?, Int?>>()
    interceptors.add(object : Interceptor<Int?, Int?> {
        override suspend fun intercept(chain: Interceptor.Chain<Int?, Int?>): Int? {
            val value = chain.input
            println("1.start : $value")
            val output = chain.proceed(value)
            println("1.end : $output")
            return output
        }
    })
    interceptors.add(object : Interceptor<Int?, Int?> {
        override suspend fun intercept(chain: Interceptor.Chain<Int?, Int?>): Int? {
            val input = chain.input
            println("2.start : $input")
            val output = chain.proceed(input?.let { it + 2 })
            println("2.end : $output")
            return output
        }
    })
    interceptors.add(object : Interceptor<Int?, Int?> {
        override suspend fun intercept(chain: Interceptor.Chain<Int?, Int?>): Int? {
            val input = chain.input
            println("3.start : $input")
            val output = chain.proceed(input?.let { it * 11 })
            println("3.end : $output")
            return output
        }
    })
    interceptors.add(object : Interceptor<Int?, Int?> {
        override suspend fun intercept(chain: Interceptor.Chain<Int?, Int?>): Int? {
            val input = chain.input
            println("4.start : $input")
            return input
        }
    })
    println("12")
    val chain = IntLoopChain(interceptors = interceptors)
    val proceed = chain.proceed(100)
    println(proceed)
    Unit
}
*/
