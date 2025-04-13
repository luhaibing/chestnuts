package com.mercer.kernel.interfaces.unity

import androidx.fragment.app.FragmentManager

/**
 * @author :Mercer
 * @Created on 2025/04/13.
 * @Description:
 *   主要用于统一在 activity、fragment 中获取 FragmentManager 相关的方法
 */
interface OnRequireFragmentManagerUnity {

    fun getSupportFragmentManager(): FragmentManager

}