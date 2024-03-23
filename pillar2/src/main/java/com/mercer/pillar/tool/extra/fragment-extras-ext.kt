@file:Suppress("unused")

package com.mskj.mercer.core.tool.extra

import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import kotlin.properties.ReadOnlyProperty

fun Fragment.stringExtraNullable(
    key: String? = null, defaultValue: String? = null,
): ReadOnlyProperty<Fragment, String?> = ReadOnlyProperty { thisRef, property ->
    thisRef.requireActivity().intent.getStringExtra(key ?: property.name) ?: defaultValue
}

fun Fragment.stringExtraNotNull(key: String? = null): ReadOnlyProperty<Fragment, String> =
    ReadOnlyProperty { thisRef, property ->
        thisRef.requireActivity().intent.getStringExtra(key ?: property.name)
            ?: throw NullPointerException("${key ?: property.name} can not be null.")
    }

fun Fragment.booleanExtra(
    key: String? = null, defaultValue: Boolean,
): ReadOnlyProperty<Fragment, Boolean> = ReadOnlyProperty { thisRef, property ->
    thisRef.requireActivity().intent.getBooleanExtra(key ?: property.name, defaultValue)
}

fun Fragment.intExtra(
    key: String? = null, defaultValue: Int,
): ReadOnlyProperty<Fragment, Int> = ReadOnlyProperty { thisRef, property ->
    thisRef.requireActivity().intent.getIntExtra(key ?: property.name, defaultValue)
}


fun Fragment.longExtra(
    key: String? = null, defaultValue: Long,
): ReadOnlyProperty<Fragment, Long> = ReadOnlyProperty { thisRef, property ->
    thisRef.requireActivity().intent.getLongExtra(key ?: property.name, defaultValue)
}

fun Fragment.floatExtra(
    key: String? = null, defaultValue: Float,
): ReadOnlyProperty<Fragment, Float> = ReadOnlyProperty { thisRef, property ->
    thisRef.requireActivity().intent.getFloatExtra(key ?: property.name, defaultValue)
}

fun Fragment.doubleExtra(
    key: String? = null, defaultValue: Double,
): ReadOnlyProperty<Fragment, Double> = ReadOnlyProperty { thisRef, property ->
    thisRef.requireActivity().intent.getDoubleExtra(key ?: property.name, defaultValue)
}

////////////////////////////////////////////////////////////////////////////////////////////////

inline fun <reified T : Serializable> Fragment.serializableExtraNullable(
    key: String? = null, defaultValue: T? = null,
): ReadOnlyProperty<Fragment, T?> = ReadOnlyProperty { thisRef, property ->
    (thisRef.requireActivity().intent
        .getSerializableExtra(key ?: property.name) as? T ?: defaultValue)
}

inline fun <reified T : Parcelable> Fragment.parcelableExtraNullable(
    key: String? = null, defaultValue: T? = null,
): ReadOnlyProperty<Fragment, T?> = ReadOnlyProperty { thisRef, property ->
    thisRef.requireActivity().intent
        .getParcelableExtra(key ?: property.name) as? T ?: defaultValue
}

inline fun <reified T : Any> Fragment.objectExtraNullable(
    key: String? = null, defaultValue: T? = null,
): ReadOnlyProperty<Fragment, T?> {
    val type = object : TypeToken<T>() {}.type
    return ReadOnlyProperty { thisRef, property ->
        val json = thisRef.requireActivity().intent.getStringExtra(key ?: property.name)
        Gson().fromJson<T>(json, type) ?: defaultValue
    }
}

inline fun <reified T : Serializable> Fragment.serializableExtraNotNull(
    key: String? = null, defaultValue: T,
): ReadOnlyProperty<Fragment, T> = ReadOnlyProperty { thisRef, property ->
    thisRef.requireActivity().intent.getSerializableExtra(key ?: property.name) as? T
        ?: throw NullPointerException("${key ?: property.name} can not be null.")
}

inline fun <reified T : Parcelable> Fragment.parcelableExtraNotNull(
    key: String? = null, defaultValue: T,
): ReadOnlyProperty<Fragment, T> = ReadOnlyProperty { thisRef, property ->
    thisRef.requireActivity().intent.getParcelableExtra(key ?: property.name) as? T
        ?: throw NullPointerException("${key ?: property.name} can not be null.")
}

inline fun <reified T : Any> Fragment.objectExtraNotNull(
    key: String? = null, defaultValue: T,
): ReadOnlyProperty<Fragment, T> {
    val type = object : TypeToken<T>() {}.type
    return ReadOnlyProperty { thisRef, property ->
        val json = thisRef.requireActivity().intent.getStringExtra(key ?: property.name)
        Gson().fromJson<T>(json, type)
            ?: throw NullPointerException("${key ?: property.name} can not be null.")
    }
}