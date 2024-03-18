package com.mskj.mercer.core.throwable.handler

/**
 * 异常处理流程
 */
interface OnThrowableHandler<Target>  {

    fun onAttach(target: Target)

    fun onHandle(throwable: Throwable)

    fun onPrompt(message: String)

}