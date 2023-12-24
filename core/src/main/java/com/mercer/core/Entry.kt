package com.mercer.core

import kotlin.reflect.KClass

annotation class Entry(
    val name: String,
    val value: KClass<out Provider>,
)