package com.mskj.mercer.core.tool

import com.blankj.utilcode.util.StringUtils
import com.mskj.mercer.core.R
import java.math.BigDecimal

fun hkd(number: Int): String {
    return hkd(number.toString())
}

fun hkd(number: Double): String {
    return hkd(number.toString())
}

fun hkd(number: Float): String {
    return hkd(number.toString())
}

fun hkd(value: BigDecimal?): String {
    return hkd((value ?: BigDecimal.ZERO).format())
}

fun hkd(value: String? = "---"): String {
    return StringUtils.getString(R.string.hkd_d, value)
}


// 格式化
fun formatToString(
    any: Any?,
    defaultPlaceholder: String = StringUtils.getString(R.string.default_holder_text)
): String {
    if (any == null) {
        return defaultPlaceholder
    }
    val toString = any.toString()
    if (toString.equals("null", true)) {
        return defaultPlaceholder
    }
    return toString
}

/**
 * @param position 0:前缀; 1:后缀
 */
fun fixTextInput(input: String, key: String, position: Int = 0): String {
    return if (position == 0) {
        if (input.startsWith(key)) {
            input.substring(key.length)
        } else {
            input
        }
    } else {
        if (input.endsWith(key)) {
            input.substring(0, input.length - key.length)
        } else {
            input
        }
    }
}