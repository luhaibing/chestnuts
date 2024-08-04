package com.mercer.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * author:  Mercer
 * date:    2024/8/4
 * desc:
 *   热流,共享数据
 */
interface OnShared<T> {

    val pipeline: CachePipeline<T>

    val currentFlow: MutableStateFlow<T?>

    val current: T?
        get() {
            return currentFlow.value
        }

}