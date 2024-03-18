package com.mskj.mercer.core.tool

import android.util.Log
import com.blankj.utilcode.util.StringUtils
import com.mskj.mercer.core.BuildConfig
import com.mskj.mercer.core.R

object Logger {

    val TAG by lazy {
        StringUtils.getString(R.string.app_name)
    }

    private fun checkBuildVariants(): Boolean = true //BuildConfig.DEBUG

    private fun print(tag: String, message: Any, block: (String, String) -> Unit) {
        if (checkBuildVariants()) {
            // 可以参照抛出异常的方法 尝试打印谁调用了
            var msg = message.toString()
            val segmentSize = 3 * 1024
            val length = msg.length;
            if (length <= segmentSize) {// 长度小于等于限制直接打印
                block(tag, msg)
            } else {
                while (msg.length > segmentSize) {// 循环分段打印日志
                    val logContent = msg.substring(0, segmentSize);
                    msg = msg.substring(segmentSize)
                    block(tag, logContent)
                }
                block(tag, msg)
            }
        }
    }

    fun e(tag: String, message: Any) {
        print(tag, message) { t, m ->
            Log.e(t, m)
        }
    }

    fun e(message: Any) {
        e(TAG, message)
    }

    fun w(tag: String, message: Any) {
        print(tag, message) { t, m ->
            Log.e(t, m)
        }
    }

    fun w(message: Any) {
        w(TAG, message)
    }

    fun i(tag: String, message: Any) {
        print(tag, message) { t, m ->
            Log.i(t, m)
        }
    }

    fun i(message: Any) {
        i(TAG, message)
    }

    fun v(tag: String, message: Any) {
        print(tag, message) { t, m ->
            Log.v(t, m)
        }
    }

    fun v(message: Any) {
        v(TAG, message)
    }


}