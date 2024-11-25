package com.mercer.kernel.interfaces.require

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

/**
 * @author :Mercer
 * @Created on 2024/06/22.
 * @Description:
 *   主要用于统一在 activity、fragment 中获取 activity 的方法
 */
interface OnRequireActivity : OnRequireContext {

    fun requireActivity(): AppCompatActivity

    override fun requireContext(): Context {
        return requireActivity()
    }

}