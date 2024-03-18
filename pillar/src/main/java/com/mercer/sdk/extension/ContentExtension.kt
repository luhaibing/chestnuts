package com.mercer.sdk.extension

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment

inline fun <reified T> Activity.startActivity(bundle: Bundle? = null) {
    val intent = Intent(this, T::class.java)
    if (bundle != null) {
        intent.putExtras(bundle)
    }
    startActivity(intent)
}

inline fun <reified T> Activity.startActivity(vararg pairs: Pair<String, Any>) {
    val intent = Intent(this, T::class.java)
    intent.putExtras(*pairs)
    startActivity(intent)
}


inline fun <reified T> Fragment.startActivity(bundle: Bundle? = null) {
    val intent = Intent(requireContext(), T::class.java)
    if (bundle != null) {
        intent.putExtras(bundle)
    }
    startActivity(intent)
}

inline fun <reified T> Fragment.startActivity(vararg pairs: Pair<String, Any>) {
    val intent = Intent(requireContext(), T::class.java)
    intent.putExtras(*pairs)
    startActivity(intent)
}