package com.mskj.mercer.core.throwable.exception

import android.content.Context
import com.blankj.utilcode.util.StringUtils
import com.mskj.mercer.core.R
import com.mskj.mercer.core.model.NetResult
import com.mskj.mercer.core.throwable.ThrowableRunnable
import com.mskj.mercer.core.throwable.handler.OnThrowableHandler

/**
 * 网络异常
 */
open class NetException(
    val code: Int, message: String?,
    val result: Any? = null
) : LocalException(message), ThrowableRunnable {

    constructor(code: Int, res: Int) : this(code, StringUtils.getString(res))

    constructor(response: NetResult<*>) : this(response.code, response.message, response.result)

    override fun process(ctx: Context, handler: OnThrowableHandler<*>) {
        if (code == 401) {
            // TODO 退出登录,清除缓存,跳转到登录页面
        }
        val m: String = if (message.isNullOrBlank()) {
            string(R.string.wangluoyichang)
        } else {
            message!!
        }
        handler.onPrompt(m)
    }

    inline fun <reified T> result() = result as? T

}