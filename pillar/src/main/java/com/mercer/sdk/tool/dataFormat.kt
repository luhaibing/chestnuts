@file:Suppress("ClassName")

package com.mskj.mercer.core.tool

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
sealed class DateFormat(private val pattern: String) {

    private val dateFormat by lazy {
        SimpleDateFormat(pattern)
    }

    fun format(date: Date): String = format(date.time)

    fun format(date: Long): String = dateFormat.format(date)

    fun parse(date: String) = try {
        dateFormat.parse(date)
    } catch (e: Exception) {
        null
    }

}


object YYYY_MM_DD_1 : DateFormat("yyyy.MM.dd")

object YYYY_MM_DD_2 : DateFormat("yyyy-MM-dd")

object YYYY_MM_DD_3 : DateFormat("yyyyMMdd")

object YYYY_MM_DD_4 : DateFormat("dd/MM/yyyy")

object YYYY_MM_DD_5 : DateFormat("yyyy/MM/dd")

object YYYY_MM_DD_HH_MM_SS_1 : DateFormat("dd/MM/yyyy HH:mm:ss")

object YYYY_MM_DD_HH_MM_SS_2 : DateFormat("yyyy.MM.dd HH:mm:ss")

object YYYY_MM_DD_HH_MM_SS_3 : DateFormat("yyyy/MM/dd HH:mm:ss")

object HH_MM_SS_YYYY_MM_DD_1 : DateFormat("HH:mm:ss yyyy/MM/dd")

object HH_MM_SS_YYYY_MM_DD_2 : DateFormat("HH:mm:ss dd/MM/yyyy")

object HH_MM_SS_YYYY_MM_DD_3 : DateFormat("HH:mm:ss dd-MM-yyyy")

object HH_MM_1 : DateFormat("HH:mm")

object HH_MM_SS_1 : DateFormat("HH:mm:ss")

/*
   val timeFormat: DateFormat by lazy {
        HH_MM_SS_YYYY_MM_DD_2
        SimpleDateFormat("HH:mm")
    }

    val dateFormat: DateFormat by lazy {
        SimpleDateFormat("yyyy.MM.dd")
    }
     val timeFormat = HH_MM_1
     val dateFormat = YYYY_MM_DD_1
 */