package com.mercer.kernel.throwable.event

import com.mercer.kernel.throwable.EventException

/**
 * @author :Mercer
 * @Created on 2025/04/11.
 * @Description:
 *   路由跳转异常
 */
class RouteToPathException(
    val path: String,
    reason: Throwable? = null
) : EventException(reason)