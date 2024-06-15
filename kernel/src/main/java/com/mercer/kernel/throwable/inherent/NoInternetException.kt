package com.mercer.kernel.throwable.inherent

import com.mercer.kernel.throwable.AbsException
import com.mercer.kernel.throwable.InherentThrowable
import com.mercer.kernel.throwable.PlainTextException
import java.io.IOException

/**
 * author:  Mercer
 * date:    2024/6/22
 * desc:
 *   无网络的异常
 */
@Suppress("MemberVisibilityCanBePrivate", "unused", "RedundantConstructorKeyword")
class NoInternetException constructor(
    val value: AbsException
) : IOException(), InherentThrowable {

    constructor(message: String) : this(PlainTextException(message))

    override fun invoke(): AbsException = value

}