package com.mercer.lib.test2

import com.mercer.core.Converter
import java.lang.reflect.Type


/**
 * @author :Mercer
 * @Created on 2024/7/30.
 * @Description:
 *
 */
class SimpleConverter : Converter {

    override fun <T> serialize(value: T): String {
        TODO("Not yet implemented")
    }

    override fun <T> deserializate(value: String, type: Type): T {
        TODO("Not yet implemented")
    }

}