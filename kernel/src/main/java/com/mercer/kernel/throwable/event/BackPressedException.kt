package com.mercer.kernel.throwable.event

import com.mercer.kernel.throwable.EventException

/**
 * @author :Mercer
 * @Created on 2024/06/22.
 * @Description:
 *   按钮返回
 */
class BackPressedException(
    reason: Throwable? = null
) : EventException(reason)