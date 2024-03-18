package com.mskj.mercer.core.throwable.exception

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mskj.mercer.core.throwable.ThrowableRunnable
import com.mskj.mercer.core.throwable.handler.OnThrowableHandler

/**
 * 结束页面的异常
 */
@Suppress("unused")
class FinishException(
    message: String? = null,
    private val resultCode: Int? = null,
    private val data: Bundle? = null,
    private val runnable: ((Context, OnThrowableHandler<*>) -> Unit)? = null
) : LocalException(message), ThrowableRunnable {


    companion object {

        fun ok(
            message: String? = null,
            runnable: ((Context, OnThrowableHandler<*>) -> Unit)? = null
        ) = FinishException(message, Activity.RESULT_OK, null, runnable)

        // 携带
        fun okCarryData(
            data: Bundle, message: String? = null,
            runnable: ((Context, OnThrowableHandler<*>) -> Unit)? = null
        ) = FinishException(message, Activity.RESULT_OK, data, runnable)

    }

    override fun process(ctx: Context, handler: OnThrowableHandler<*>) {
        runnable?.invoke(ctx, handler)
        if (!message.isNullOrBlank()) {
            handler.onPrompt(message)
        }
        (ctx as? AppCompatActivity)?.apply {
            if (resultCode != null) {
                if (data == null) {
                    setResult(resultCode)
                } else {
                    setResult(resultCode, Intent().putExtras(data))
                }
            }
            finish()
        }
    }

}