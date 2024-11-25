package com.mercer.kernel.interfaces.require

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * @author :Mercer
 * @Created on 2024/06/22.
 * @Description:
 *   主要用于统一在 Activity、fragment、ViewModel 中获取 CoroutineScope 相关的方法
 */
interface OnRequireCoroutineScope {

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

    fun <T : Any?> Flow<T>.observe(block: suspend (T) -> Unit): Job = consume(block)

}