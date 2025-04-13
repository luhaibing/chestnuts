package com.mercer.kernel.interfaces.unity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

/**
 * @author :Mercer
 * @Created on 2025/04/13.
 * @Description:
 *   主要用于统一在 activity、fragment 中获取 activity 的方法
 */
interface OnRequireActivityUnity : OnRequireContextUnity {

    fun requireActivity(): AppCompatActivity

    override fun requireContext(): Context {
        return requireActivity()
    }

}