@file:Suppress("unused")

package com.mercer.kernel.throwable.event

import androidx.annotation.IdRes
import com.mercer.kernel.throwable.EventException

/**
 * @author :Mercer
 * @Created on 2024/06/22.
 * @Description:
 *   导航跳转
 */
class NavigateException(
    @IdRes val target: Int,
    reason: Throwable? = null
) : EventException(reason)