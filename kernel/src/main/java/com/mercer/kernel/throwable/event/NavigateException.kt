package com.mercer.kernel.throwable.event

import androidx.annotation.IdRes
import com.mercer.kernel.throwable.EventException

/**
 * @author :Mercer
 * @Created on 2025/04/11.
 * @Description:
 *   导航跳转
 */
class NavigateException(
    @IdRes val navigationRes: Int,
    reason: Throwable? = null
) : EventException(reason)