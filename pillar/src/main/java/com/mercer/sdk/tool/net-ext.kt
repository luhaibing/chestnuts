package com.mskj.mercer.core.tool

import com.mskj.mercer.core.R
import com.mskj.mercer.core.model.NetResult
import com.mskj.mercer.core.throwable.exception.NetException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

fun <T : NetResult<*>> Flow<T>.checkCode(): Flow<T> = onEach {
    if (!it.success()) {
        throw NetException(it)
    }
}

fun <N : Any, T : NetResult<N>> Flow<T>.conversionResult() = map {
    if (it.result == null) {
        throw NetException(it.code, R.string.shujuweikong)
    }
    it.result
}

fun <N : Any, T : NetResult<N>> Flow<T>.conversionResultNullable() = map {
    it.result
}

fun <T : Any> Flow<T>.execute(throwable: Throwable) = onEach {
    throw throwable
}
fun <T : Any> Flow<T>.executeCatch(throwable: Throwable) = catch {
    throw throwable
}
