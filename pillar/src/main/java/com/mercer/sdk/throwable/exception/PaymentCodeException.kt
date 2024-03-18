package com.mskj.mercer.core.throwable.exception

import com.mskj.mercer.core.model.NetResult
import com.mskj.mercer.core.throwable.exception.PaymentCodeException.Companion.MAX_TIMES
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class PaymentCodeException(
    code: Int, message: String?, val times: Int
) : NetException(code, message, times) {
    companion object {
        const val MAX_TIMES = 5
    }
}

inline fun <reified T : Any?> Flow<NetResult<T>>.convertPaymentCodeException() = catch {
    if (it is NetException && it.code == 250) {
        throw PaymentCodeException(
            code = it.code,
            message = it.message,
            times = it.result<Int>() ?: MAX_TIMES
        )
    }
    throw it
}