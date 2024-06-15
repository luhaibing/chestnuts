package com.mercer.kernel.throwable.event

import com.mercer.kernel.throwable.EventException

/**
 * author:  Mercer
 * date:    2024/6/22
 * desc:
 *   用户异常登出/账户下线
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class LogoutException(
    val value: Int
) : EventException() {

    companion object {
        const val FLAG_TOKEN_EXPIRATION = 0x0
        const val FLAG_MULTI_DEVICE = 0x1
        val TOKEN_EXPIRATION = LogoutException(FLAG_TOKEN_EXPIRATION)
        val MULTI_DEVICE = LogoutException(FLAG_MULTI_DEVICE)
    }

}