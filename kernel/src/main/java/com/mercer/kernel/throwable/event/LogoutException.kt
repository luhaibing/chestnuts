package com.mercer.kernel.throwable.event

import com.mercer.kernel.throwable.EventException

/**
 * @author :Mercer
 * @Created on 2025/04/11.
 * @Description:
 *   用户异常登出/账户下线
 */
class LogoutException(val value: Int) : EventException() {

    companion object {
        // 主动登出
        const val FLAG_LOGOUT = 0x0
        // token 过期
        const val FLAG_TOKEN_EXPIRATION = 0x1
        // 其他设备登陆
        const val FLAG_OTHER_DEVICE_LOGIN = 0x2

        val LOGOUT = LogoutException(FLAG_LOGOUT)
        val TOKEN_EXPIRATION = LogoutException(FLAG_TOKEN_EXPIRATION)
        val OTHER_DEVICE_LOGIN = LogoutException(FLAG_OTHER_DEVICE_LOGIN)
    }

}