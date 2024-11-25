package com.mercer.kernel.throwable

/**
 * @author :Mercer
 * @Created on 2024/06/22.
 * @Description:
 *   内部异常
 *   项目内自定义异常的超类以及基础的几个异常
 */
sealed class AbsException(cause: Throwable?) : RuntimeException(cause)
