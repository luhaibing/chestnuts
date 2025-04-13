package com.mercer.kernel.extension

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import com.mercer.kernel.R
import androidx.core.graphics.createBitmap

/**
 * @author :Mercer
 * @Created on 2025/04/13.
 * @Description:
 *   视图的扩展
 */

/**
 * 控件添加点击效果
 */
fun View.pressedBackgroundDrawable(color: Int? = null) {
    val pressedColor: Int = color ?: ContextCompat.getColor(context, R.color.pressed_color)
    val drawable = background
    val stateList = backgroundTintList
    if (drawable == null && stateList == null) {
        return
    }
    // backgroundTintList 优先级高于 background
    if (stateList != null) {
        val attrs = arrayOf(
            android.R.attr.state_pressed,
            android.R.attr.state_focused,
            android.R.attr.state_selected,
            android.R.attr.state_window_focused,
            android.R.attr.state_enabled,
            android.R.attr.state_activated,
            android.R.attr.state_hovered,
            android.R.attr.state_drag_can_accept,
            android.R.attr.state_drag_hovered,
        )
        val defaultColor = stateList.defaultColor
        val colors = Array(attrs.size) {
            val attr = attrs[it]
            if (attr == android.R.attr.state_pressed) {
                pressedColor
            } else {
                stateList.getColorForState(intArrayOf(attr), defaultColor)
            }
        }
        val states = Array(attrs.size) {
            intArrayOf(attrs[it])
        }
        backgroundTintList = ColorStateList(arrayOf(*states, intArrayOf(0)), intArrayOf(*colors.toIntArray(), stateList.defaultColor))
    } else {
        val onGlobalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val stateListDrawable = StateListDrawable()
                val defaultFrame = if (drawable is StateListDrawable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val index = drawable.findStateDrawableIndex(intArrayOf())
                    drawable.getStateDrawable(index) ?: drawable.current
                } else {
                    drawable.current
                }
                val bitmap = createBitmap(width, height,Bitmap.Config.ARGB_8888)
                defaultFrame.setBounds(0, 0, width, height)
                Canvas(bitmap).also {
                    defaultFrame.draw(it)
                }
                val bitmapDrawable = bitmap.toDrawable(resources)
                bitmapDrawable.setTint(pressedColor)
                val pressedDrawable = LayerDrawable(arrayOf(defaultFrame, bitmapDrawable))
                stateListDrawable.addState(intArrayOf(android.R.attr.state_pressed), pressedDrawable)
                stateListDrawable.addState(intArrayOf(0), drawable)
                background = stateListDrawable
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }
        viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)
    }
}
