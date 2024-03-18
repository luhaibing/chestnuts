package com.mskj.mercer.core.tool


import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import kotlin.properties.ReadOnlyProperty

fun Fragment.stringArgumentsNullable(
    key: String? = null, defaultValue: String? = null,
): ReadOnlyProperty<Fragment, String?> {
    var v: String? = null
    return ReadOnlyProperty { thisRef, property ->
        v ?: (thisRef.arguments?.getString(key ?: property.name) ?: defaultValue).also {
            v = it
        }
    }
}

fun Fragment.stringArgumentsNotNull(key: String? = null): ReadOnlyProperty<Fragment, String> {
    var v: String? = null
    return ReadOnlyProperty { thisRef, property ->
        v ?: (thisRef.arguments?.getString(key ?: property.name)
            ?: throw NullPointerException("${key ?: property.name} can not be null.")).also {
            v = it
        }
    }
}

fun Fragment.booleanArguments(
    key: String? = null, defaultValue: Boolean,
): ReadOnlyProperty<Fragment, Boolean> {
    var v: Boolean? = null
    return ReadOnlyProperty { thisRef, property ->
        v ?: (thisRef.arguments?.getBoolean(key ?: property.name) ?: defaultValue).also {
            v = it
        }
    }
}

fun Fragment.intArguments(
    key: String? = null, defaultValue: Int,
): ReadOnlyProperty<Fragment, Int> {
    var v: Int? = null
    return ReadOnlyProperty { thisRef, property ->
        v ?: (thisRef.arguments?.getInt(key ?: property.name) ?: defaultValue).also {
            v = it
        }
    }
}


fun Fragment.longArguments(
    key: String? = null, defaultValue: Long,
): ReadOnlyProperty<Fragment, Long> {
    var v: Long? = null
    return ReadOnlyProperty { thisRef, property ->
        v ?: (thisRef.arguments?.getLong(key ?: property.name) ?: defaultValue).also {
            v = it
        }
    }
}

fun Fragment.floatArguments(
    key: String? = null, defaultValue: Float,
): ReadOnlyProperty<Fragment, Float> {
    var v: Float? = null
    return ReadOnlyProperty { thisRef, property ->
        v ?: (thisRef.arguments?.getFloat(key ?: property.name) ?: defaultValue).also {
            v = it
        }
    }
}

fun Fragment.doubleArguments(
    key: String? = null, defaultValue: Double,
): ReadOnlyProperty<Fragment, Double> {
    var v: Double? = null
    return ReadOnlyProperty { thisRef, property ->
        v ?: (thisRef.arguments?.getDouble(key ?: property.name) ?: defaultValue).also {
            v = it
        }
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////

inline fun <reified T : Serializable> Fragment.serializableArgumentsNullable(
    key: String? = null, defaultValue: T? = null,
): ReadOnlyProperty<Fragment, T?> {
    var v: T? = null
    return ReadOnlyProperty { thisRef, property ->
        v ?: (thisRef.arguments?.getSerializable(key ?: property.name) as? T ?: defaultValue).also {
            v = it
        }
    }
}

inline fun <reified T : Parcelable> Fragment.parcelableArgumentsNullable(
    key: String? = null, defaultValue: T? = null,
): ReadOnlyProperty<Fragment, T?> {
    var v: T? = null
    return ReadOnlyProperty { thisRef, property ->
        v ?: (thisRef.arguments?.getParcelable(key ?: property.name) as? T ?: defaultValue).also {
            v = it
        }
    }
}

inline fun <reified T : Any> Fragment.objectArgumentsNullable(
    key: String? = null, defaultValue: T? = null,
): ReadOnlyProperty<Fragment, T?> {
    val type = object : TypeToken<T>() {}.type
    var v: T? = null
    return ReadOnlyProperty { thisRef, property ->
        if (v != null) {
            return@ReadOnlyProperty v
        }
        val json = thisRef.arguments?.getString(key ?: property.name)
        (Gson().fromJson<T>(json, type) ?: defaultValue).also {
            v = it
        }
    }
}

inline fun <reified T : Serializable> Fragment.serializableArgumentsNotNull(
    key: String? = null, defaultValue: T,
): ReadOnlyProperty<Fragment, T> {
    var v: T? = null
    return ReadOnlyProperty { thisRef, property ->
        v ?: (thisRef.arguments?.getSerializable(key ?: property.name) as? T ?: defaultValue).also {
            v = it
        }
    }
}

inline fun <reified T : Parcelable> Fragment.parcelableArgumentsNotNull(
    key: String? = null, defaultValue: T,
): ReadOnlyProperty<Fragment, T> {
    var v: T? = null
    return ReadOnlyProperty { thisRef, property ->
        v ?: (thisRef.arguments?.getParcelable(key ?: property.name) as? T ?: defaultValue).also {
            v = it
        }
    }
}

inline fun <reified T : Any> Fragment.objectArgumentsNotNull(
    key: String? = null, defaultValue: T,
): ReadOnlyProperty<Fragment, T> {
    val type = object : TypeToken<T>() {}.type
    var v: T? = null
    return ReadOnlyProperty { thisRef, property ->
        val json = thisRef.arguments?.getString(key ?: property.name)
        v ?: (Gson().fromJson<T>(json, type) ?: defaultValue).also {
            v = it
        }
    }
}