package com.mercer.sdk.ui.unify

import android.content.Context

/**
 * author:  mercer
 * date:    2024/3/17 04:38
 * desc:
 *   主要用于统一在 activity、fragment 中获取上下文的方法
 */
interface OnRequireContextUnify {

    fun requireContext(): Context

}
