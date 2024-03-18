package com.mskj.mercer.core.tool

import android.text.InputFilter
import android.util.Log
import com.blankj.utilcode.util.StringUtils
import com.mskj.mercer.core.R
import java.math.BigDecimal


/**
 * 获取字符数量 汉字占2个，英文占一个
 * @param input
 * @return
 */
fun getInputLength(input: CharSequence?): Int {
    var length = 0
    if (input.isNullOrBlank()) {
        return length
    }
    for (element in input) {
        if (element.toInt() > 255) {
            length += 2
        } else {
            length++
        }
    }
    return length
}

/**
 * 限制字符长度
 * 输入字符串的长度限定
 */
fun inputLengthLimit(maxLength: Int): InputFilter {
    return InputFilter { source, _, _, dest, _, _ ->
        if (source.isEmpty()) {
            return@InputFilter null
        }
        val length = getInputLength(dest)
        var space = maxLength - length
        val content = StringBuilder()
        for (element in source) {
            val size = if (element.toInt() > 255) 2 else 1
            space -= size
            if (space < 0) {
                break
            }
            content.append(element)
        }
        return@InputFilter content
    }
}

//输入的金额范围
fun inputAmountLimit(min: Number = BigDecimal.ZERO, max: Number): InputFilter {
    val minValue = if (min is BigDecimal) {
        min
    } else {
        BigDecimal.valueOf(min.toDouble())
    }
    val maxValue = if (max is BigDecimal) {
        max
    } else {
        BigDecimal.valueOf(max.toDouble())
    }

    val minLastIndexOf = min.toString().length - 1 - min.toString().lastIndexOf(".")
    val maxLastIndexOf = max.toString().length - 1 - max.toString().lastIndexOf(".")
    val offset = minLastIndexOf.coerceAtLeast(maxLastIndexOf)
    return inputNumberLimit(min, max, offset)
}


/**
 * @param min   最小值
 * @param max   最大值
 * @param exact 小数点后几位
 */
fun inputNumberLimit(min: Number = BigDecimal.ZERO, max: Number, exact: Int): InputFilter {

    val minValue = if (min is BigDecimal) {
        min
    } else {
        BigDecimal.valueOf(min.toDouble())
    }
    val maxValue = if (max is BigDecimal) {
        max
    } else {
        BigDecimal.valueOf(max.toDouble())
    }

    return InputFilter { source, start, end, dest, destStart, destEnd ->
        // 拼接出真实的输入
        val header = dest.substring(0, destStart)
        val footer = dest.substring(destStart)
        val inputValue = header + source + footer
        val message = exact
        val lastIndexOf = inputValue.lastIndexOf('.')

        if (exact <= 0 && source == ".") {
            return@InputFilter ""
        }

        if (lastIndexOf != -1 && inputValue.length - 1 - lastIndexOf > exact) {
            return@InputFilter ""
        }

        return@InputFilter try {
            val value = BigDecimal(inputValue)
            if (value < minValue || value > maxValue) {
                return@InputFilter ""
            }
            if (value == BigDecimal.ZERO && inputValue.length == 1) {
                return@InputFilter source
            }
            if (value == BigDecimal.ZERO && lastIndexOf != -1 && inputValue.length == 2 + exact) {
                if (inputValue.all { it == '.' && it == '0' }) {
                    return@InputFilter ""
                }
            } else if (value == BigDecimal.ZERO && lastIndexOf == -1 && inputValue.length == 2) {
                if (inputValue.all { it == '0' }) {
                    return@InputFilter ""
                }
            }
            source
        } catch (e: Exception) {
            Log.w(StringUtils.getString(R.string.app_name), e.printStackTrace().toString())
            return@InputFilter ""
        }
    }
}

