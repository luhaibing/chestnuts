package com.mercer.sdk.pojo

import androidx.annotation.Keep

@Keep
data class NetResult<T>(
    val code: Int,
    val message: String?,
    val result: T?
)  {
    fun success() = code == 200
    // 失败
    fun failure() = code != 200
}
