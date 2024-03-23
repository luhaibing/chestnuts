package com.mercer.pillar.model

import androidx.annotation.Keep

/**
 * author:  mercer
 * date:    2024/3/23 00:09
 * desc:
 *   响应
 */
@Keep
data class Response<out T : Any>(
    val code: Int,
    val message: String?,
    val result: T?
) {

    /**
     * 是否成功
     */
    fun check() = code == 200

}
