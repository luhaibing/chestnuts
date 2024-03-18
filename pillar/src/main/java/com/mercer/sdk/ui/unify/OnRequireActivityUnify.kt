package com.mercer.sdk.ui.unify

import android.app.Activity

/**
 * author:  mercer
 * date:    2024/3/17 04:39
 * desc:
 *   主要用于统一在 activity、fragment 中获取 activity 的方法
 */
interface OnRequireActivityUnify {

    fun requireActivity(): Activity

}
