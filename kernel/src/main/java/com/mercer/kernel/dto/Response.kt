package com.mercer.kernel.dto

/**
 * @author :Mercer
 * @Created on 2025/04/11.
 * @Description:
 *   网络响应
 */
interface Response<T> {

    // 响应码
    val code: Int

    // 响应信息
    val message: String?

    // 响应数据主体
    val data: T?

    // 是否成功
    val succeed: Boolean

}