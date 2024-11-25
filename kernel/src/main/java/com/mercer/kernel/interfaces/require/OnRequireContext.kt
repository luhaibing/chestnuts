package com.mercer.kernel.interfaces.require

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

/**
 * @author :Mercer
 * @Created on 2024/06/22.
 * @Description:
 *   主要用于统一在 activity、fragment 中获取上下文的方法
 */
interface OnRequireContext {

    fun requireContext(): Context

    /////////////////////////////////////////////////// 获取资源

    fun string(@StringRes resId: Int, vararg formatArgs: Any?): String {
        return requireContext().getString(resId, *formatArgs)
    }

    fun drawable(@DrawableRes resId: Int): Drawable? {
        return ContextCompat.getDrawable(requireContext(), resId)
    }

    fun color(@ColorRes resId: Int): Int {
        return ContextCompat.getColor(requireContext(), resId)
    }

    fun dimension(@DimenRes resId: Int): Float {
        return ResourcesCompat.getFloat(requireContext().resources, resId)
    }

}