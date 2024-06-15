package com.mercer.kernel.dto

import androidx.annotation.Keep

/**
 * author:  Mercer
 * date:    2024/6/22
 * desc:
 *   网络响应
 */
@Keep
data class Response<out T : Any>(
    val code: Int,
    val message: String?,
    val result: T?
)