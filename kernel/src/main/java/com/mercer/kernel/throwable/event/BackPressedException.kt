package com.mercer.kernel.throwable.event

import com.mercer.kernel.throwable.EventException

/**
 * @author :Mercer
 * @Created on 2025/04/11.
 * @Description:
 *   物理返回键
 */
class BackPressedException(reason: Throwable? = null) : EventException(reason)