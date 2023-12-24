package com.mercer.core

interface WithKey {
    val key: String
}

sealed interface Argument<out V> {

    val value: V

    data class Query<V>(override val key: String, override val value: V) : Argument<V>, WithKey

    data class QueryMap<V>(override val value: V) : Argument<V>

    data class Field<V>(override val key: String, override val value: V) : Argument<V>, WithKey

    data class FieldMap<V>(override val value: V) : Argument<V>

    data class Part<V>(override val key: String, override val value: V) : Argument<V>, WithKey

    data class PartMap<V>(override val value: V) : Argument<V>

    data class Header<V>(override val key: String, override val value: V) : Argument<V>, WithKey

    data class HeaderMap<V>(override val value: V) : Argument<V>

    data class Body<V>(override val value: V) : Argument<V>

}
// ...