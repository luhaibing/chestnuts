package com.mercer.kernel.interfaces.throwable

import android.content.Context
import android.net.ParseException
import androidx.annotation.StringRes
import com.google.gson.JsonParseException
import com.mercer.kernel.R
import com.mercer.kernel.interfaces.interceptor.Interceptor
import com.mercer.kernel.throwable.PlainTextException
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException


/**
 * @author :Mercer
 * @Created on 2024/06/22.
 * @Description:
 *   常规异常转换器
 */
class RegularExceptionConverter (
    private val context: Context
) : ThrowableConsumer {

    override suspend fun intercept(chain: Interceptor.Chain<Throwable?, Throwable?>): Throwable? {
        return chain.input.let {
            when (it) {
                is HttpException -> {
                    PlainTextException(
                        value = string(
                            R.string.error_network_connection_error,
                            it.code().toString()
                        ),
                        reason = it
                    )
                }

                is JsonParseException, is JSONException, is ParseException -> {
                    PlainTextException(value = string(R.string.error_parse_failed), reason = it)
                }

                is ConnectException, is UnknownHostException -> {
                    PlainTextException(
                        value = string(R.string.error_network_is_unavailable),
                        reason = it
                    )
                }

                is SSLHandshakeException -> {
                    PlainTextException(
                        value = string(R.string.error_certificate_validation_failed),
                        reason = it
                    )
                }

                is SocketTimeoutException -> {
                    PlainTextException(
                        value = string(R.string.error_connection_timeout),
                        reason = it
                    )
                }

                is IllegalArgumentException -> {
                    PlainTextException(
                        value = string(R.string.error_parameter_incorrect),
                        reason = it
                    )
                }

                else -> {
                    it
                }
            }
        }.let {
            chain.proceed(it)
        }
    }

    private fun string(@StringRes value: Int, vararg formatArgs: String): String {
        return context.resources.getString(value, formatArgs)
    }

}