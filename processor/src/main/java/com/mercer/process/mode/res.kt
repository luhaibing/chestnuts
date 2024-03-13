package com.mercer.process.mode

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import kotlin.reflect.KClass

/**
 * author:  mercer
 * date:    2024/2/17 12:52
 * desc:
 *   附加
 */
data class AppendRes(
    val name: String?,
    val annotation: ClassName,
    val memberFormat: String?,
    val provider: ClassName,
) {
    fun toName(): String {
        return annotation.toString() + "_" + name
    }

    companion object {
        const val FORMAT_1 = "value = %S"
    }
}

data class PathRes(
    val typeName: TypeName,
    val value: String,
) {

    constructor(kClass: KClass<*>, value: String) : this(kClass.asTypeName(), value)

}