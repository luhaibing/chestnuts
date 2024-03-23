package com.mercer.pillar.tool

import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.TextView

// 明文密文切换
fun TextView.togglePlainOrCipher(it: Boolean) {
    transformationMethod = if (it) {
        PasswordTransformationMethod.getInstance()
    } else {
        HideReturnsTransformationMethod.getInstance()
    }
}