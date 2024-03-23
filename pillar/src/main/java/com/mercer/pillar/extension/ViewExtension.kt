package com.mercer.pillar.extension

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.mercer.pillar.R


/**
 * 弹出软键盘
 */
fun View.showSoftInput() {
    requestFocus()
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

/**
 * 隐藏软键盘
 */
fun View.hideSoftInput() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

///////////////////

inline fun <reified T> View.setTag(value: T, tag: Int = R.id.view_tag) {
    setTag(tag, value)
}

inline fun <reified T> View.getTag(tag: Int = R.id.view_tag): T? {
    return getTag(tag) as? T
}

inline fun <reified T> View.getTag(defaultValue: T, tag: Int = R.id.view_tag): T {
    return getTag(tag) as? T ?: defaultValue
}
