package com.mskj.mercer.core.tool

import android.text.Editable
import java.math.BigDecimal
import java.text.DecimalFormat

/**
 * 金额格式化
 */
fun BigDecimal?.format(pattern: String = "0,000.00"): String {
    if (this == null) {
        return BigDecimal.ZERO.toString()
    }
    val p = if (this < 1000.toBigDecimal()) {
        // pattern.substring(pattern.lastIndexOf(",") + 1)
        var indexOf = pattern.lastIndexOf(".")
        if (indexOf == -1) {
            indexOf = pattern.length
        }
        try {
            pattern.substring(indexOf - this.toInt().toString().length)
        }catch (_:Exception){
            pattern
        }
    } else {
        pattern
    }
    return DecimalFormat(p).format(this)
}

fun Number?.format(pattern: String = "0.00"): String = this?.toDouble()?.toBigDecimal().format(pattern)

fun String?.bigDecimal(defaultValue: BigDecimal? = BigDecimal.ZERO) = try {
    if (this.isNullOrEmpty()) {
        defaultValue
    } else {
        toBigDecimal()
    }
} catch (e: Exception) {
    defaultValue
}

fun Editable?.bigDecimal() = this?.toString().bigDecimal(null)