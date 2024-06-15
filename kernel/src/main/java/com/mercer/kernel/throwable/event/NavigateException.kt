package com.mercer.kernel.throwable.event

import androidx.annotation.IdRes
import com.mercer.kernel.throwable.EventException

/**
 * author:  Mercer
 * date:    2024/6/22
 * desc:
 *   导航跳转
 */
class NavigateException(
    @IdRes val target: Int,
    reason: Throwable? = null
) : EventException(reason)