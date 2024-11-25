package com.mercer.kernel.interfaces.throwable

import com.mercer.kernel.interfaces.interceptor.Interceptor
import com.mercer.kernel.interfaces.interceptor.LoopChain
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * @author :Mercer
 * @Created on 2024/06/22.
 * @Description:
 *   异常处理
 */
/*
 * 异常处理的拦截器
 */

typealias ThrowableConsumer = Interceptor<Throwable?, Throwable?>

/**
 * 异常处理链
 */
class ThrowableLoopChain private constructor(
    position: Int = 0,
    values: List<ThrowableConsumer>
) : LoopChain<Throwable?, Throwable?>(position, values) {

    override suspend fun proceed(value: Throwable?): Throwable? {
        if (value == null) {
            return null
        }
        return super.proceed(value)
    }

    override fun next(): LoopChain<Throwable?, Throwable?> {
        return ThrowableLoopChain(current + 1, interceptors).also {
            it.input = input
        }
    }

    companion object {

        operator fun invoke(vararg values: ThrowableConsumer): ThrowableLoopChain {
            return ThrowableLoopChain(0, values.asList())
        }

        operator fun invoke(interceptors: List<ThrowableConsumer>): ThrowableLoopChain {
            return invoke(StartThrowableConverter, *interceptors.toTypedArray(), EndThrowableConsumer)
        }

    }

}

/*
 * 下发/分发/调度 Throwable
 */
interface ThrowableDispatcher {


    /*
     * 下发/分发/调度
     */
    suspend fun dispatch(value: Throwable): Throwable? {
        return unhandledException(value)
    }

    /**
     * 未处理的异常
     */
    suspend fun unhandledException(value: Throwable): Throwable? {
        value.printStackTrace()
        return value
    }

    /*
     * 下发/分发/调度
     */
    // fun tryDispatch(value: Throwable): ChannelResult<Unit>

}

/*
 * 异常处理
 */
interface OnThrowableHandle {

    val throwableConsumer: List<ThrowableConsumer>

    suspend fun handle(value: Throwable): Throwable? {
        return handle(value, throwableConsumer)
    }

    suspend fun handle(value: Throwable, interceptors: List<ThrowableConsumer>): Throwable? {
        return handle(value, ThrowableLoopChain(*interceptors.toTypedArray()))
    }

    suspend fun handle(value: Throwable, chain: ThrowableLoopChain): Throwable? {
        return chain.proceed(value)
    }

}

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
