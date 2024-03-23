package com.mercer.pillar.interfaces.support

import kotlinx.coroutines.flow.Flow

/**
 * author:  mercer
 * date:    2024/3/23 00:08
 * desc:
 *   异常的处理和传递
 */

// 处理异常
interface OnThrowableHandle {

    /**
     * 处理异常
     */
    fun handleThrowable(value: Throwable)

}

// 导出/转接异常
interface OnThrowableExport {

    fun deliverThrowable(): Flow<Throwable>

}