package com.mskj.mercer.core.throwable.handler

import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.JsonParseException
import com.mskj.mercer.core.R
import com.mskj.mercer.core.throwable.ThrowableRunnable
import com.mskj.mercer.core.throwable.exception.CancelException
import com.mskj.mercer.core.throwable.exception.LocalException
import com.mskj.mercer.core.throwable.exception.NetException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException


class ActivityThrowableHandlerImpl : OnThrowableHandler<AppCompatActivity> {

    private lateinit var activity: AppCompatActivity

    override fun onAttach(target: AppCompatActivity) {
        activity = target
    }

    override fun onHandle(throwable: Throwable) {
        throwable.printStackTrace()

        if (throwable is CancelException) {
            // 不做任何响应
            return
        }

        // 协程挂起取消的抛出的异常
        if (throwable is CancellationException) {
            // 不做任何响应
            return
        }

        if (throwable is NetException && throwable.code == 401) {
            // 被挤下线
            // 不做任何响应
            return
        }
        if (throwable is HttpException && throwable.code() == 401) {
            // 被挤下线
            // 不做任何响应
            return
        }

        // TODO: 2021/7/24 0024 待扩展
        when (throwable) {
            is ThrowableRunnable -> {
                // 内部消化
                throwable.process(activity, this)
            }
            is LocalException -> {
                if (!throwable.message.isNullOrBlank()) {
                    onPrompt(throwable.message!!)
                }
            }
            is ConnectException -> {
                // 连接失败
                onPrompt(StringUtils.getString(R.string.network_connection_failed))
            }
            is SocketTimeoutException -> {
                // 请求超时
                onPrompt(StringUtils.getString(R.string.network_request_timeout))
            }
            is JsonParseException -> {
                // 数据解析错误
                onPrompt(StringUtils.getString(R.string.api_data_parse_error))
            }
            else -> {
                onPrompt(throwable.message ?: StringUtils.getString(R.string.wangluoyichang))
            }
        }

    }

    override fun onPrompt(message: String) {
        ToastUtils.showShort(message)
    }

}