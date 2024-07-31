package com.mercer.core

/**
 * author:  Mercer
 * date:    2024/7/28
 * desc:
 *   数据
 */
enum class Source {
    CACHE, NETWORK
}

data class Result<out T>(
    val value: T,
    val source: Source
)