package com.mercer.kernel.throwable.flow

import com.mercer.kernel.throwable.event.BackPressedException
import com.mercer.kernel.throwable.event.LogoutException
import com.mercer.kernel.throwable.event.NavigateException
import com.mercer.kernel.throwable.event.RouteToPathException

/**
 * @author :Mercer
 * @Created on 2025/04/13.
 * @Description:
 *   事件类异常处理器
 */

/*
* 物理返回键
*/
class BackPressedExceptionConsumer(
    action: (BackPressedException) -> Boolean
) : EventExceptionConsumer<BackPressedException>(BackPressedException::class, action)

/*
 * 账户登出
 */
class LogoutExceptionConsumer(
    action: (LogoutException) -> Boolean
) : EventExceptionConsumer<LogoutException>(LogoutException::class, action)

/*
 * 页面导航
 */
class NavigateExceptionConsumer(
    action: (NavigateException) -> Boolean
) : EventExceptionConsumer<NavigateException>(NavigateException::class, action)


class RouteToPathExceptionConsumer(
    action: (RouteToPathException) -> Boolean
) : EventExceptionConsumer<RouteToPathException>(RouteToPathException::class, action)