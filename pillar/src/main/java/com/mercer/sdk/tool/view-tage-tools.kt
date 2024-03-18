package com.mskj.mercer.core.tool

import android.view.View
import com.mskj.mercer.core.R

inline fun <reified T> View.value( value: T,tag: Int = R.id.view_tag,) = setTag(tag, value)

inline fun <reified T> View.valueNotNull( defaultValue: T,tag: Int = R.id.view_tag,): T =
    valueNullable(tag) ?: defaultValue

inline fun <reified T> View.valueNullable(tag: Int = R.id.view_tag): T? = getTag(tag) as? T