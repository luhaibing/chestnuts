package com.mercer.library.model

import android.os.Build
import android.os.ext.SdkExtensions.getExtensionVersion

/**
 * author:  mercer
 * date:    2024/3/22 23:07
 * desc:
 *
 */
//compileSdkVersion 需要至少为 33 才可以调用此方法
fun isPhotoPickerAvailable(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        true
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        getExtensionVersion(Build.VERSION_CODES.R) >= 2
    } else {
        false
    }
}
