package com.mercer.pillar.throwable.exceptions

/**
 * author:  mercer
 * date:    2024/3/23 00:49
 * desc:
 *   简单操作-返回
 */
data class BackPressedException(
    val resultCode: Int,
    override val message: String?,
) : LocalException(message)