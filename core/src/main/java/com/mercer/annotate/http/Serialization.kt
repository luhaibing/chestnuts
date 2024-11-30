package com.mercer.annotate.http

import com.mercer.core.Converter
import kotlin.reflect.KClass

/**
 * @author :Mercer
 * @Created on 2024/11/27.
 * @Description:
 *   序列化
 */
annotation class Serialization(val value: KClass<out Converter.Factory<*>>)