package com.mercer.sdk.ui.unify

import kotlinx.coroutines.CoroutineScope

/**
 * author:  mercer
 * date:    2024/3/14 02:54
 * desc:
 *   主要用于统一在 Activity、fragment、ViewModel 中获取 CoroutineScope 相关的方法
 *
 */
interface OnCoroutineScopeUnify {

    fun requireCoroutineScope(): CoroutineScope

}