package com.mercer.kernel.extension

import androidx.fragment.app.Fragment

/**
 * @author :Mercer
 * @Created on 2025/04/13.
 * @Description:
 *   fragment 扩展
 */

/*
 * 寻找 Fragment 的父节点
 */
inline fun <reified T> Fragment.parentAs(): T? {
    var node: Fragment? = this
    var find: T?
    do {
        node = node?.parentFragment
        find = node as? T
    } while (find == null && node?.parentFragment != null)
    return find ?: requireActivity() as? T
}