@file:Suppress("unused")

package com.mskj.mercer.core.tool.extra

import android.app.Activity
import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import kotlin.properties.ReadOnlyProperty


fun Activity.stringExtraNullable(
    key: String? = null, defaultValue: String? = null
): ReadOnlyProperty<Activity, String?> = ReadOnlyProperty { thisRef, property ->
    thisRef.intent.getStringExtra(key ?: property.name) ?: defaultValue
}

fun Activity.stringExtraNotNull(key: String? = null): ReadOnlyProperty<Activity, String> =
    ReadOnlyProperty { thisRef, property ->
        thisRef.intent.getStringExtra(key ?: property.name)
            ?: throw NullPointerException("${key ?: property.name} can not be null.")
    }

fun Activity.booleanExtra(
    key: String? = null, defaultValue: Boolean = false
): ReadOnlyProperty<Activity, Boolean> = ReadOnlyProperty { thisRef, property ->
    thisRef.intent.getBooleanExtra(key ?: property.name, defaultValue)
}

fun Activity.intExtra(
    key: String? = null, defaultValue: Int = 0
): ReadOnlyProperty<Activity, Int> = ReadOnlyProperty { thisRef, property ->
    thisRef.intent.getIntExtra(key ?: property.name, defaultValue)
}

fun Activity.longExtra(
    key: String? = null, defaultValue: Long = 0L
): ReadOnlyProperty<Activity, Long> = ReadOnlyProperty { thisRef, property ->
    thisRef.intent.getLongExtra(key ?: property.name, defaultValue)
}

fun Activity.floatExtra(
    key: String? = null, defaultValue: Float = 0F
): ReadOnlyProperty<Activity, Float> = ReadOnlyProperty { thisRef, property ->
    thisRef.intent.getFloatExtra(key ?: property.name, defaultValue)
}

fun Activity.doubleExtra(
    key: String? = null, defaultValue: Double = 0.toDouble()
): ReadOnlyProperty<Activity, Double> = ReadOnlyProperty { thisRef, property ->
    thisRef.intent.getDoubleExtra(key ?: property.name, defaultValue)
}

////////////////////////////////////////////////////////////////////////////////////////////////

inline fun <reified T : Serializable> Activity.serializableExtraNullable(
    key: String? = null, defaultValue: T? = null
): ReadOnlyProperty<Activity, T?> = ReadOnlyProperty { thisRef, property ->
    thisRef.intent.getSerializableExtra(key ?: property.name) as? T ?: defaultValue
}

inline fun <reified T : Parcelable> Activity.parcelableExtraNullable(
    key: String? = null, defaultValue: T? = null
): ReadOnlyProperty<Activity, T?> = ReadOnlyProperty { thisRef, property ->
    thisRef.intent.getParcelableExtra(key ?: property.name) as? T ?: defaultValue
}

inline fun <reified T : Parcelable> Activity.parcelableArrayListExtraNullable(
    key: String? = null, defaultValue: List<T>? = null
): ReadOnlyProperty<Activity, List<T>?> = ReadOnlyProperty { thisRef, property ->
    (thisRef.intent.getParcelableArrayListExtra<T>(
        key ?: property.name
    ) as? List<T>)?.toMutableList() ?: defaultValue
}

inline fun <reified T : Any> Activity.objectExtraNullable(
    key: String? = null, defaultValue: T? = null
): ReadOnlyProperty<Activity, T?> {
    val type = object : TypeToken<T>() {}.type
    return ReadOnlyProperty { thisRef, property ->
        val json = thisRef.intent.getStringExtra(key ?: property.name)
        Gson().fromJson<T>(json, type) ?: defaultValue
    }
}

inline fun <reified T : Serializable> Activity.serializableExtraNotNull(
    key: String? = null, value: T? = null
): ReadOnlyProperty<Activity, T> {
    var v: T? = null
    return ReadOnlyProperty { thisRef, property ->
        if (v == null) {
            v = thisRef.intent.getSerializableExtra(key ?: property.name) as? T ?: value
        }
        return@ReadOnlyProperty v
            ?: throw NullPointerException("${key ?: property.name} can not be null.")
    }
}

inline fun <reified T : Parcelable> Activity.parcelableExtraNotNull(
    key: String? = null, value: T? = null
): ReadOnlyProperty<Activity, T> {
    var v: T? = null
    return ReadOnlyProperty { thisRef, property ->
        if (v == null) {
            v = thisRef.intent.getParcelableExtra(key ?: property.name) as? T ?: value
        }
        return@ReadOnlyProperty v
            ?: throw NullPointerException("${key ?: property.name} can not be null.")
    }
}

inline fun <reified T : Parcelable> Activity.parcelableArrayListExtraNotNull(
    key: String? = null, value: List<T>? = null
): ReadOnlyProperty<Activity, List<T>> {
    var v: List<T>? = null
    return ReadOnlyProperty { thisRef, property ->
        if (v == null) {
            v = (thisRef.intent.getParcelableArrayListExtra<T>(
                key ?: property.name
            ) as? List<T>)?.toMutableList() ?: value
        }
        return@ReadOnlyProperty v
            ?: throw NullPointerException("${key ?: property.name} can not be null.")
    }
}

// inline fun <reified T : Parcelable> Activity.parcelableArrayListExtraNullable(
//    key: String? = null, defaultValue: List<T>? = null
//): ReadOnlyProperty<Activity, List<T>?> = ReadOnlyProperty { thisRef, property ->
//    thisRef.intent.getParcelableArrayListExtra<T>(key ?: property.name) as? List<T> ?: defaultValue
//}

inline fun <reified T : Any> Activity.objectExtraNotNull(
    key: String? = null, value: T? = null
): ReadOnlyProperty<Activity, T> {
    val type = object : TypeToken<T>() {}.type
    var v: T? = null
    return ReadOnlyProperty { thisRef, property ->
        val json = thisRef.intent.getStringExtra(key ?: property.name)
        if (v == null) {
            v = Gson().fromJson<T>(json, type) ?: value
        }
        return@ReadOnlyProperty v
            ?: throw NullPointerException("${key ?: property.name} can not be null.")
    }
}
