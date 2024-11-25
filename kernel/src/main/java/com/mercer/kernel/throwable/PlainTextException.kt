package com.mercer.kernel.throwable

/**
 * @author :Mercer
 * @Created on 2024/06/22.
 * @Description:
 *   明文类异常
 *   可以直接提示给用户
 */
open class PlainTextException(val value: String, reason: Throwable? = null) : AbsException(reason)
