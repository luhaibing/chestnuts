package com.mskj.mercer.core.throwable

import android.content.Context
import com.mskj.mercer.core.throwable.handler.OnThrowableHandler

/**
 * 实现类改类的异常 自己内部处理消化
 */
interface ThrowableRunnable {

    fun process(ctx:Context,handler: OnThrowableHandler<*>)

}