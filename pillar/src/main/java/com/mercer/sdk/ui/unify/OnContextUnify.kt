package com.mercer.sdk.ui.unify

import android.app.Activity
import android.content.Context

/**
 * author:  mercer
 * date:    2024/3/14 01:32
 * desc:
 *   主要用于统一在 activity、fragment 中获取上下文的方法
 */
interface OnContextUnify {

    fun requireContext(): Context

    fun requireActivity(): Activity

}