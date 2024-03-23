package com.mercer.pillar.pojo

import androidx.annotation.Keep

/**
 * author:  mercer
 * date:    2024/3/23 00:10
 * desc:
 *   结果
 */
@Keep
sealed class Result<out T : Any> {

    /**
     * 成功
     */
    @Keep
    data class Success<out T : Any>(val value: T) : Result<T>()

    /**
     * 失败
     */
    @Keep
    data class Failure(val cause: Exception) : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[value=$value]"
            is Failure -> "Failure[cause=$cause]"
        }
    }

}