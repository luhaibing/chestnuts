package com.mercer.pillar.throwable.exceptions

/**
 * author:  mercer
 * date:    2024/3/23 00:40
 * desc:
 *   账户下线
 */
data class LogoutException(val value: Int) : LocalException(null) {

    companion object {
        const val FLAG_TOKEN_EXPIRATION = 0x0
        const val FLAG_MULTI_DEVICE = 0x1
        val TOKEN_EXPIRATION = LogoutException(FLAG_TOKEN_EXPIRATION)
        val MULTI_DEVICE = LogoutException(FLAG_MULTI_DEVICE)
    }

}