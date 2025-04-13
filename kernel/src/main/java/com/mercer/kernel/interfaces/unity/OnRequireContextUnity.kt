package com.mercer.kernel.interfaces.unity

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
 * @Created on 2025/04/13.
 * @Description:
 *   主要用于统一在 activity、fragment 中获取 上下文 和 资源 的方法
 */
interface OnRequireContextUnity {

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