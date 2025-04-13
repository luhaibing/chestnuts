package com.mercer.kernel.extension

import com.mercer.kernel.dto.Response
import com.mercer.kernel.throwable.plaintext.ResponseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

/**
 * @author :Mercer
 * @Created on 2025/04/13.
 * @Description:
 *   网络响应的Flow扩展
 */

inline fun <reified T : Any?> Flow<Response<T>>.require(crossinline predicate: ((Response<T>) -> Boolean)): Flow<Response<T>> {
    return onEach {
        if (predicate(it).not()) {
            throw ResponseException(it)
        }
    }
}

inline fun <reified T : Any?> Flow<Response<T>>.requireCode(value: Int = 200): Flow<Response<T>> {
    return require { it.code == value }
}


inline fun <reified T : Any?> Flow<Response<T>>.requireSucceed(): Flow<Response<T>> {
    return require { it.succeed }
}

inline fun <reified T : Any?> Flow<Response<T>>.requireBody(): Flow<T> {
    return require {
        it.succeed
    }.map {
        it.data as T
    }
}