package com.mercer.kernel.throwable

/**
 * @author :Mercer
 * @Created on 2025/04/10.
 * @Description:
 *   特殊情况的异常
 *   例如: OkHttp 拦截器内只能跑出 IO相关异常
 */
interface InherentThrowable {
    operator fun invoke(): AbsException
}