package com.mskj.mercer.core.tool

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


inline fun <reified T> String.asType(): T = Gson().fromJson(this, object : TypeToken<T>() {}.type)

fun Any?.asJson(): String = Gson().toJson(this)
