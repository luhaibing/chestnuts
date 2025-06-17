package com.mercer.test.lib

import okhttp3.Interceptor
import okhttp3.Response

/**
 * author:  Mercer
 * date:    2024/08/10
 * desc:
 *   自定义的拦截器
 */

class MyInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return chain.proceed(request)
    }

}