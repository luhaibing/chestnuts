package com.mercer.process.mode

import com.squareup.kotlinpoet.ClassName

/**
 * author:  mercer
 * date:    2024/2/17 12:52
 * desc:
 *   附加
 */
data class AppendRes(
    val name: String?,
    val annotation: ClassName,
    val memberFormat: String?,
    val provider: ClassName,
) {
    fun toName(): String {
        return annotation.toString() + "_" + name
    }

    companion object {
        const val FORMAT_1 = "value = %S"
    }
}