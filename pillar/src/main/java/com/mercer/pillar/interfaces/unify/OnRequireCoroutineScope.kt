package com.mercer.pillar.interfaces.unify

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * author:  mercer
 * date:    2024/3/23 00:00
 * desc:
 *   主要用于统一在 Activity、fragment、ViewModel 中获取 CoroutineScope 相关的方法
 */
interface OnRequireCoroutineScope {

    fun requireCoroutineScope(): CoroutineScope

    ///////////////////////////////////////////////////

    /**
     * 订阅
     */
    fun <T> Flow<T>.collect(block: suspend (T) -> Unit) {
        requireCoroutineScope().launch {
            collect {
                block(it)
            }
        }
    }

    /**
     * 订阅
     */
    fun <T> Flow<T>.observe() {
        requireCoroutineScope().launch {
            collect()
        }
    }

}