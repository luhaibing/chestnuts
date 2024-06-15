package com.mercer.kernel.throwable.plaintext

import com.mercer.kernel.throwable.PlainTextException

/**
 * author:  Mercer
 * date:    2024/6/22
 * desc:
 *   网络交互/请求异常
 *   网络响应异常
 */
class ResponseException(
    val code: Int,
    value: String,
) : PlainTextException(value, null)