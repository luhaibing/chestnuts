package com.mercer.kernel.throwable.flow

import com.mercer.kernel.interfaces.interceptor.coroutines.Interceptor
import com.mercer.kernel.interfaces.interceptor.coroutines.LoopChain
import com.mercer.kernel.throwable.CancelException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * @author :Mercer
 * @Created on 2025/04/13.
 * @Description:
 *   异常处理流程
 */

/*
* 异常处理的拦截器
*/
typealias ThrowableConsumer = Interceptor<Throwable, Throwable?>
typealias ThrowableLoopChain = LoopChain<Throwable, Throwable?>


interface OnThrowableHandler {

    val throwableConsumers: List<ThrowableConsumer>

    /**
     * 初始化异常消费者的列表
     */
    fun initializeThrowableHandler() {
    }

    /**
     * 处理异常
     */
    suspend fun handle(value: Throwable): Throwable? {
        return handle(value, throwableConsumers)
    }

    /**
     * 处理异常
     */
    suspend fun handle(value: Throwable, interceptors: List<ThrowableConsumer>): Throwable? {
        val values = arrayListOf<ThrowableConsumer>()
        values.add(StartThrowableConverter)
        values.add(RegularExceptionConverter(::regularExceptionConvert))
        values.addAll(interceptors)
        values.add(EndThrowableConsumer)
        val chain = LoopChain(position = 0, interceptors = values)
        return handle(value, chain)
    }

    /**
     * 处理异常
     */
    suspend fun handle(value: Throwable, chain: ThrowableLoopChain): Throwable? {
        val throwable = try {
            chain.proceed(value)
        } catch (e: Exception) {
            e.printStackTrace()
            ProceedFailedException(e, value)
        }
        return throwable?.let { unhandled(it) }
    }

    /**
     * 未处理的异常
     */
    suspend fun unhandled(value: Throwable): Throwable? {
        value.printStackTrace()
        return value
    }

    /**
     * 常规异常转化器
     */
    fun regularExceptionConvert(value: Throwable): Throwable? {
        return value
    }

}

/**
 * 处理失败的异常
 */
class ProceedFailedException(val value: Throwable?, cause: Throwable) : CancelException(cause)

/*
 * 异常转发
 */
interface OnThrowableForward {

    val channel: Channel<Throwable>

    /*
     * 自身处理不了的 Throwable,转发给其他部分
     */
    fun derivedOf(): Flow<Throwable> = channel.receiveAsFlow()

}