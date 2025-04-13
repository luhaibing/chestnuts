package com.mercer.kernel.throwable

/**
 * @author :Mercer
 * @Created on 2025/04/11.
 * @Description:
 *   中断/暂停 后续程序 的异常
 *   本身没有意义
 */
open class CancelException(cause: Throwable? = null) : AbsException(cause)