package com.mercer.kernel.throwable.plaintext

import com.mercer.kernel.throwable.PlainTextException

/**
 * @author :Mercer
 * @Created on 2024/06/22.
 * @Description:
 *   明文类异常
 */
class ResponseException(val code: Int, value: String) : PlainTextException(value, null)