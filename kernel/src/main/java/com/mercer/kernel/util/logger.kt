package com.mercer.kernel.util

import android.util.Log
import com.mercer.kernel.BuildConfig

/**
 * author:  Mercer
 * date:    2024/6/22
 * desc:
 *   日志打印
 */

/*
VERBOSE（冗长）： 用 Log.v() 记录。这是最低的一种级别，用于记录任何对调试或开发过程中有帮助的信息。
DEBUG（调试）： 用 Log.d() 记录。比 VERBOSE 稍微高一点的级别，用于调试程序，记录那些在开发阶段有助于调试，但在实际用户使用过程中不需要的信息。
INFO（信息）： 用 Log.i() 记录。用于记录一般信息，这些信息可能对最终用户和开发者都有用。
WARN（警告）： 用 Log.w() 记录。警告级别用于记录可能会导致问题的情况，但并不一定表示程序功能出现了错误。
ERROR（错误）： 用 Log.e() 记录。这是记录那些非预期失败或程序错误的日志级别，比如捕获到的异常。
ASSERT（断言）： 用 Log.wtf() 记录。这个级别表明一个非常严重的问题，它总是指出那些不应该发生的情况。Log.wtf() 的 "wtf" 代表 "What a Terrible Failure" （多么可怕的失败）。
*/


interface Printer {
    fun print(level: Level, tag: String, message: String, throwable: Throwable? = null)
    enum class Level {
        VERBOSE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
        ASSERT,
    }
}

class DefaultPrinter : Printer {
    override fun print(level: Printer.Level, tag: String, message: String, throwable: Throwable?) {
        if (!BuildConfig.DEBUG) {
            return
        }
        when (level) {
            Printer.Level.VERBOSE -> Log.e(tag, message, throwable)
            Printer.Level.DEBUG -> Log.d(tag, message, throwable)
            Printer.Level.INFO -> Log.i(tag, message, throwable)
            Printer.Level.WARN -> Log.w(tag, message, throwable)
            Printer.Level.ERROR -> Log.e(tag, message, throwable)
            Printer.Level.ASSERT -> Log.wtf(tag, message, throwable)
        }
    }
}

var printer: Printer = DefaultPrinter()
var DEFAULT_TAG = "TAG"

fun v(message: String, throwable: Throwable? = null, tag: String = DEFAULT_TAG) {
    printer.print(
        level = Printer.Level.VERBOSE,
        tag = tag,
        message = message,
        throwable = throwable
    )
}

fun d(message: String, throwable: Throwable? = null, tag: String = DEFAULT_TAG) {
    printer.print(
        level = Printer.Level.DEBUG,
        tag = tag,
        message = message,
        throwable = throwable
    )
}

fun i(message: String, throwable: Throwable? = null, tag: String = DEFAULT_TAG) {
    printer.print(
        level = Printer.Level.INFO,
        tag = tag,
        message = message,
        throwable = throwable
    )
}

fun w(message: String, throwable: Throwable? = null, tag: String = DEFAULT_TAG) {
    printer.print(
        level = Printer.Level.WARN,
        tag = tag,
        message = message,
        throwable = throwable
    )
}

fun e(message: String, throwable: Throwable? = null, tag: String = DEFAULT_TAG) {
    printer.print(
        level = Printer.Level.ERROR,
        tag = tag,
        message = message,
        throwable = throwable
    )
}

fun wtf(message: String, throwable: Throwable? = null, tag: String = DEFAULT_TAG) {
    printer.print(
        level = Printer.Level.ASSERT,
        tag = tag,
        message = message,
        throwable = throwable
    )
}


fun String.v() = v(this)

fun String.d() = d(this)

fun String.i() = i(this)

fun String.w() = w(this)

fun String.e() = e(this)