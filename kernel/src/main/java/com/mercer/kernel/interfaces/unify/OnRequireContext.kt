package com.mercer.kernel.interfaces.unify

import android.content.Context

/**
 * author:  Mercer
 * date:    2024/6/22
 * desc:
 *   主要用于统一在 activity、fragment 中获取上下文的方法
 */
interface OnRequireContext {

    fun requireContext(): Context

    val Int.dp: Int
        get() {
            return toFloat().dp.toInt()
        }

    val Float.dp: Float
        get() {
            val density = requireContext().resources.displayMetrics.density
            return this * density + 0.5f
        }

}