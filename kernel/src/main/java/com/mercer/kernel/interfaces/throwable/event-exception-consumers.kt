package com.mercer.kernel.interfaces.throwable

import com.mercer.kernel.throwable.event.BackPressedException
import com.mercer.kernel.throwable.event.LogoutException
import com.mercer.kernel.throwable.event.NavigateException

/**
 * @author :Mercer
 * @Created on 2024/06/22.
 * @Description:
 *   事件类异常处理器
 */


/*
 * 物理返回键
 */
class BackPressedExceptionConsumer(
    action: (BackPressedException) -> Unit
) : EventExceptionConsumer<BackPressedException>(BackPressedException::class, action)

/*
 * 账户登出
 */
class LogoutExceptionConsumer(
    action: (LogoutException) -> Unit
) : EventExceptionConsumer<LogoutException>(LogoutException::class, action)

/*
 * 页面导航
 */
class NavigateExceptionConsumer(
    action: (NavigateException) -> Unit
) : EventExceptionConsumer<NavigateException>(NavigateException::class, action)
