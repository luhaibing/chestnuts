package com.mercer.kernel.interfaces.unify

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

/**
 * author:  Mercer
 * date:    2024/6/22
 * desc:
 *   主要用于统一在 activity、fragment 中获取 activity 的方法
 */
interface OnRequireActivity : OnRequireContext {

    fun requireActivity(): AppCompatActivity

    override fun requireContext(): Context = requireActivity()

}