package com.mercer.kernel.throwable

/**
 * @author :Mercer
 * @Created on 2025/04/10.
 * @Description:
 *   明文类异常
 *   默认对应 Toast / Snackbar 信息
 */
open class PlainTextException(val value: String, reason: Throwable? = null) : AbsException(reason)