package com.mercer.kernel.throwable.inherent

import com.mercer.kernel.throwable.AbsException
import com.mercer.kernel.throwable.InherentThrowable
import com.mercer.kernel.throwable.PlainTextException
import java.io.IOException

/**
 * @author :Mercer
 * @Created on 2025/04/11.
 * @Description:
 *   无网络异常
 */
class NoInternetException constructor(
    val value: AbsException
) : IOException(), InherentThrowable {

    constructor(message: String) : this(PlainTextException(message))

    override fun invoke(): AbsException = value

}