package com.mercer.kernel.throwable

/**
 * @author :Mercer
 * @Created on 2024/06/22.
 * @Description:
 *   中断/暂停 后续程序 的异常
 */
open class CancelException(cause: Throwable? = null) : AbsException(cause)
