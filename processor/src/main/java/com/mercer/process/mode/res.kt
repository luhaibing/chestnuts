package com.mercer.process.mode

import com.mercer.core.Strategy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import kotlin.reflect.KClass

/**
 * author:  Mercer
 * date:    2024/8/10
 * desc:
 *
 */
data class PathRes(
    val typeName: TypeName,
    val value: String,
    val flag: Int,
) {

    constructor(kClass: KClass<*>, value: String, flag: Int)
            : this(kClass.asTypeName(), value, flag)

}

data class AppendRes(
    val annotation: KClass<out Annotation>,
    val key: String,
    val providerTypeName: TypeName,
    val returnTypeName: TypeName,
    val flags: List<Int>,
) {
    val unique: String
        get() {
            return annotation.asTypeName().toString() + "," + key
        }
}

interface OnPipeline {
    val pipeline: TypeName
}

data class CacheRes(
    override val pipeline: TypeName,
    val strategy: Strategy
) : OnPipeline

data class StateRes(
    override val pipeline: TypeName,
) : OnPipeline