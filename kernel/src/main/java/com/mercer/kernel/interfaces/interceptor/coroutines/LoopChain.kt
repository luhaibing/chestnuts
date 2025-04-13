package com.mercer.kernel.interfaces.interceptor.coroutines

import kotlinx.coroutines.runBlocking


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

    override suspend fun proceed(value: In): Out {
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


class IntLoopChain(
    index: Int = 0,
    interceptors: List<Interceptor<Int, Int?>>
) : LoopChain<Int, Int?>(index, interceptors) {

    override suspend fun proceed(value: Int): Int? {
        return super.proceed(value)
    }

}

fun main() = runBlocking {
    val interceptors = arrayListOf<Interceptor<Int, Int?>>()
    interceptors.add(object : Interceptor<Int, Int?> {
        override suspend fun intercept(chain: Interceptor.Chain<Int, Int?>): Int? {
            val value = chain.input
            println("1.start : $value")
            val output = chain.proceed(value)
            println("1.end : $output")
            return output
        }
    })
    interceptors.add(object : Interceptor<Int, Int?> {
        override suspend fun intercept(chain: Interceptor.Chain<Int, Int?>): Int? {
            val input = chain.input
            println("2.start : $input")
            val output = chain.proceed(input + 2)
            println("2.end : $output")
            return output
        }
    })
    interceptors.add(object : Interceptor<Int, Int?> {
        override suspend fun intercept(chain: Interceptor.Chain<Int, Int?>): Int? {
            val input = chain.input
            println("3.start : $input")
            val output = chain.proceed(input * 11)
            println("3.end : $output")
            return output
        }
    })
    interceptors.add(object : Interceptor<Int, Int?> {
        override suspend fun intercept(chain: Interceptor.Chain<Int, Int?>): Int? {
            val input = chain.input
            println("4.start : $input")
            val output = chain.proceed(input * 100)
            return output
        }
    })
    val chain = IntLoopChain(interceptors = interceptors)
    val proceed = chain.proceed(100)
    println(proceed)
}

