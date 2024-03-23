package com.mercer.pillar.interfaces.unify

import android.content.Context

/**
 * author:  mercer
 * date:    2024/3/22 23:59
 * desc:
 *   主要用于统一在 activity、fragment 中获取上下文的方法
 */
interface OnRequireContext {

    fun requireContext(): Context

}