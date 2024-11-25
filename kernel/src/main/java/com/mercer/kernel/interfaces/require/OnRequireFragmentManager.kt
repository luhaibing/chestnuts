package com.mercer.kernel.interfaces.require

import androidx.fragment.app.FragmentManager

/**
 * @author :Mercer
 * @Created on 2024/06/22.
 * @Description:
 *   主要用于统一在 activity、fragment 中获取 FragmentManager 相关的方法
 */
interface OnRequireFragmentManager {

    fun getSupportFragmentManager(): FragmentManager

}