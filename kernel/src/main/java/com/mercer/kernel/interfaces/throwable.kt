package com.mercer.kernel.interfaces

import com.mercer.kernel.interfaces.interceptor.Interceptor
import com.mercer.kernel.interfaces.interceptor.LoopChain
import com.mercer.kernel.throwable.InherentThrowableConverter
import com.mercer.kernel.throwable.consumers.LastThrowableConsumer
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * author:  Mercer
 * date:    2024/6/22
 * desc:
 *   异常处理
 */

interface ThrowableInterceptor : Interceptor<Throwable, Throwable?>

/**
 * 异常的处理
 */
/*
interface HandledThrowable : OnRequireCoroutineScope {

    // 保底策略
    val throwableHandler: ThrowableInterceptor

    // 拦截节点
    val throwableInterceptors: ThrowableInterceptor

    fun handle(value: Throwable) {
        requireCoroutineScope().launch {
            onPreProcess(value).let {
                if (onInterceptThrowable(it)) null else it
            }?.let {
                if (it is InherentThrowable) it() else it
            }?.let {
                if (it is CombinedException) it.expand() else arrayListOf(it)
            }?.onEach {
                // 串行处理
                LoopChain.start(it, throwableHandler, throwableInterceptors)
            }
        }
    }

    // 提前处理
    suspend fun onPreProcess(value: Throwable): Throwable? = null

    // 判断是否拦截该抛出
    fun onInterceptThrowable(value: Throwable?): Boolean = false
}
*/

interface HandledThrowable {

    // 第一个消费者
    val firstConsumer: ThrowableInterceptor

    // 拦截节点
    val throwableInterceptors: List<ThrowableInterceptor>

    // 最后的消费者
    val lastConsumer: ThrowableInterceptor

    suspend fun handle(value: Throwable): Throwable? {
        val result = LoopChain.start(value, arrayListOf<ThrowableInterceptor>().apply {
            add(firstConsumer)
            addAll(throwableInterceptors)
            add(lastConsumer)
        })
        return result
    }

}

class HandledThrowableImpl : HandledThrowable {

    override val firstConsumer: ThrowableInterceptor = InherentThrowableConverter

    override val throwableInterceptors: MutableList<ThrowableInterceptor> by lazy {
        arrayListOf()
    }

    override val lastConsumer: ThrowableInterceptor by lazy {
        LastThrowableConsumer()
    }

}

/**
 * 自身处理不了的异常 导出/转接
 */
interface DerivedThrowable {

    fun derivedOf(): Flow<Throwable>

    /**
     * 下发/分发/调度
     */
    suspend fun dispatch(value: Throwable)

}

class DerivedThrowableImpl : DerivedThrowable {

    private val channel = Channel<Throwable>()

    override fun derivedOf(): Flow<Throwable> {
        return channel.receiveAsFlow()
    }

    override suspend fun dispatch(value: Throwable) {
        channel.send(value)
    }

}