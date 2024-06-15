package com.mercer.kernel.interfaces.unify

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

/**
 * author:  Mercer
 * date:    2024/6/22
 * desc:
 *   主要用于统一在 Activity、fragment、ViewModel 中获取 CoroutineScope 相关的方法
 */
interface OnRequireCoroutineScope {

    fun requireCoroutineScope(): CoroutineScope

    ///////////////////////////////////////////////////

    /**
     * 订阅/消费
     */
    fun <T> Flow<T>.consume(block: suspend (T) -> Unit) {
        requireCoroutineScope().launch {
            collect {
                block(it)
            }
        }
    }

    /**
     * 订阅
     */
    fun <T> Flow<T>.launch() {
        launchIn(requireCoroutineScope())
    }

}