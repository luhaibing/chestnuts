package com.mercer.kernel.throwable.plaintext

import com.mercer.kernel.dto.Response
import com.mercer.kernel.throwable.PlainTextException

/**
 * @author :Mercer
 * @Created on 2025/04/11.
 * @Description:
 *   响应异常
 */
class ResponseException(val response: Response<*>) : PlainTextException(response.message ?: "", null) {

    class InternalResponse(
        override val message: String
    ) : Response<Nothing> {
        override val code: Int = 0
        override val data: Nothing? = null
        override val succeed: Boolean = false
    }

    constructor(message: String) : this(InternalResponse(message))

}