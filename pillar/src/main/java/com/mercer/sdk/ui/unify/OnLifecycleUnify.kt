package com.mercer.sdk.ui.unify

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

/**
 * author:  mercer
 * date:    2024/3/14 02:27
 * desc:
 *   主要用于统一在 activity、fragment 中获取 Lifecycle 相关的方法
 */
interface OnLifecycleUnify {

    fun requireLifecycle(): Lifecycle

    fun requireLifecycleOwner(): LifecycleOwner

}