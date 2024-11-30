package com.mercer.process.mode

import com.google.devtools.ksp.symbol.ClassKind
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import kotlin.reflect.KClass

/**
 * author:  Mercer
 * date:    2024/2/17
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
    val providerTypeName: TypeNameSnapshot,
    val returnTypeName: TypeName,
    val flags: List<Int>,
) {
    val unique: String
        get() {
            return annotation.asTypeName().toString() + "," + key
        }
}

data class TypeNameSnapshot(
    val value: TypeName,
    val classKind: ClassKind
) {
    val isObject: Boolean
        get() {
            return classKind == ClassKind.OBJECT
        }
}

data class PersistenceRes(
    val persistence: TypeNameSnapshot,
    val dispatcher: TypeNameSnapshot,
)

typealias SerializationRes = TypeNameSnapshot