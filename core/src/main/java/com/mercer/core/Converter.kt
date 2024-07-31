package com.mercer.core

import java.lang.reflect.Type


/**
 * @author :Mercer
 * @Created on 2024/7/30.
 * @Description:
 *
 */
interface Converter {

    /**
     *
     */
    fun <T> serialize(value: T): String

    /**
     * 反序列化
     */
    fun <T> deserializate(value: String, type: Type): T

}