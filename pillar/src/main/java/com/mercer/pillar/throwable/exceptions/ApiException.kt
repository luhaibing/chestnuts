package com.mercer.pillar.throwable.exceptions

/**
 * author:  mercer
 * date:    2024/3/23 00:39
 * desc:
 *   网络交互/请求异常
 */
data class ApiException(
    private val code: Int,
    override val message: String,
) : LocalException(message)