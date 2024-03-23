@file:Suppress("unused")

package com.mskj.mercer.core.tool

import androidx.annotation.IntRange
import java.util.*

/**
 * 时间戳的扩展函数
 * https://www.jianshu.com/p/b8fad7e47bd0
 */
// 方向
enum class Direction(
    val value: Int
) {
    /**
     * 之前
     */
    BEFORE(-1),

    /**
     * 之后
     */
    AFTER(+1),
}

val BEFORE = Direction.BEFORE

val AFTER = Direction.AFTER

/**
 * 日历
 */
fun calendar(): Calendar {
    return Calendar.getInstance()
}

/**
 * 当前的时间戳
 */
private fun Calendar.currentTime(): Long {
    return time.time
}

infix fun Int.day(direction: Direction): Long {
    return calendar().apply {
        add(Calendar.DAY_OF_YEAR, this@day * direction.value)
    }.currentTime()
}

/**
 * 月份
 */
infix fun Int.month(direction: Direction): Long {
    return calendar().apply {
        add(Calendar.MONTH, this@month * direction.value)
    }.currentTime()
}

/**
 * 年
 */
infix fun Int.year(direction: Direction): Long {
    return calendar().apply {
        add(Calendar.YEAR, this@year * direction.value)
    }.currentTime()
}

/**
 * 周
 */
infix fun Int.week(direction: Direction): Long {
    return calendar().apply {
        add(Calendar.YEAR, this@week * direction.value)
    }.currentTime()
}

/**
 * 小时
 */
infix fun Int.hour(direction: Direction): Long {
    return calendar().apply {
        add(Calendar.HOUR_OF_DAY, this@hour * direction.value)
    }.currentTime()
}

/**
 * 分钟
 */
infix fun Int.minute(direction: Direction): Long {
    return calendar().apply {
        add(Calendar.MINUTE, this@minute * direction.value)
    }.currentTime()
}

/**
 * 秒
 */
infix fun Int.second(direction: Direction): Long {
    return calendar().apply {
        add(Calendar.SECOND, this@second * direction.value)
    }.currentTime()
}

infix fun Long.ath(@IntRange(from = 0, to = 23) value: Int): Long = at(Point(value, 0, 0))

infix fun Long.atm(@IntRange(from = 0, to = 59) value: Int): Long = at(Point(null, value, 0))

infix fun Long.ats(@IntRange(from = 0, to = 59) value: Int): Long =
    at(Point(null, null, value))

/**
 * 获取当天的指定时间点的时间戳
 */
infix fun Long.at(value: Point): Long {
    return calendar().apply {
        timeInMillis = this@at
        value.hour?.let {
            set(Calendar.HOUR_OF_DAY, it)
        }
        value.minute?.let {
            set(Calendar.MINUTE, it)
        }
        value.second?.let {
            set(Calendar.SECOND, it)
        }
        set(Calendar.MILLISECOND, (value.millisecond ?: 0))
    }.currentTime()
}

infix fun Calendar.at(value: Point): Long =
    currentTime().at(value)

infix fun Date.at(value: Point): Long =
    time.at(value)

/**
 * 时间点
 */
data class Point(
    @IntRange(from = 0, to = 23) val hour: Int?,
    @IntRange(from = 0, to = 59) val minute: Int?,
    @IntRange(from = 0, to = 59) val second: Int?,
    @IntRange(from = 0, to = 999) val millisecond: Int? = null,
)

// 只保留 时分秒[注:早上8点前的事件为负数值]
fun Long.hmsByDay(): Long {
    val c = calendar();
    c.timeInMillis = this
    c.set(1970, 0, 1)
    return c.timeInMillis
}

// 一秒钟
const val SECOND: Long = 1000L

// 一分钟
const val MIN: Long = 60 * SECOND

// 一个小时
const val HOUR: Long = 60 * MIN

// 一天
const val DAY: Long = 24 * HOUR