package com.mercer.core

import kotlinx.coroutines.flow.StateFlow

/**
 * author:  Mercer
 * date:    2024/8/9
 * desc:
 *   热流,xx
 */
interface OnState<T> {
    val pipeline: Pipeline<T?>
    val currentFlow: StateFlow<T?>
    val current: T?
        get() {
            return currentFlow.value
        }

    /**
     * 默认值,如果未重写该方法,即使在 子类中指定 T 为非空,生成的代码中的 T ,也还是会为非空类型
     */
    fun defaultValue(): T? = null

    val path: Path
}