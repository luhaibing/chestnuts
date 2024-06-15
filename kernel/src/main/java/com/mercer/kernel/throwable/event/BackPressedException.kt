package com.mercer.kernel.throwable.event

import com.mercer.kernel.throwable.EventException

/**
 * author:  Mercer
 * date:    2024/6/22
 * desc:
 *   按键返回
 */
@Suppress("unused")
class BackPressedException(
    reason: Throwable? = null
) : EventException(reason)