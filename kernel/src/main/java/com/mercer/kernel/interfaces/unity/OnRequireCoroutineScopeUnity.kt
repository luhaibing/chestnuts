package com.mercer.kernel.interfaces.unity

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * @author :Mercer
 * @Created on 2025/04/13.
 * @Description:
 *   1
 */
interface OnRequireCoroutineScopeUnity {

    val coroutineScope: CoroutineScope

    fun requireCoroutineScope(): CoroutineScope {
        return coroutineScope
    }

    ///////////////////////////////////////////////////

    /*
     * 订阅
     */
    fun <T> Flow<T>.launch(): Job {
        return launchIn(requireCoroutineScope())
    }

    ///////////////////////////////////////////////////

    /*
     * 订阅/消费
     */
    fun <T : Any?> Flow<T>.consume(block: suspend (T) -> Unit): Job {
        return onEach {
            block(it)
        }.launch()
    }

    ///////////////////////////////////////////////////

    /**
     * 传递给另一个流
     */
    fun <T : Any?> Flow<T>.observeTo(flowCollector: FlowCollector<T>): Flow<T> = onEach {
        flowCollector.emit(it)
    }

}