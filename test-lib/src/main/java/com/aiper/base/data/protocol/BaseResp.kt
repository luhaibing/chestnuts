package com.aiper.base.data.protocol

/**
 * @Created on 2023/2/28.
 * @author :破天荒
 * @Description:
 */
open class BaseResp<T>(
    val code: Int = 0,
    val successful: Boolean = false,
    val message: String? = null,
    var data: T? = null
) {
    fun requestSuccess(): Boolean {
        return successful
    }

    fun requestFailedNoToast(): Boolean {
        return successful
    }

    fun parseData(): T? {
        requireSuccess()
        return data
    }

    private fun requireSuccess() {

    }
}

