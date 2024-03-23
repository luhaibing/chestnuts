package com.mercer.pillar.interfaces.unify

import android.app.Activity

/**
 * author:  mercer
 * date:    2024/3/22 23:59
 * desc:
 *   主要用于统一在 activity、fragment 中获取 activity 的方法
 */
interface OnRequireActivity {

    fun requireActivity(): Activity

}