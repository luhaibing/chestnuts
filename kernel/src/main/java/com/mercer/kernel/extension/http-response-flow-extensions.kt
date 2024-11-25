package com.mercer.kernel.extension

import com.mercer.kernel.dto.net.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

/**
 * @author :Mercer
 * @Created on 2024/06/22.
 * @Description:
 *   网络响应的Flow扩展
 */

inline fun <reified T : Any> Flow<Response<T>>.require(crossinline predicate: ((Response<T>) -> Boolean), crossinline block: () -> Unit) = onEach {
    if (predicate(it).not()) {
        block()
    }
}

inline fun <reified T : Any> Flow<Response<T>>.requireCode(value: Int, crossinline block: () -> Unit): Flow<Response<T>> {
    return require({
        it.code == value
    }, block)
}


inline fun <reified T : Any> Flow<Response<T>>.requireSucceed(): Flow<Response<T>> {
    return require({
        it.succeed
    }, {
        TODO()
    })
}