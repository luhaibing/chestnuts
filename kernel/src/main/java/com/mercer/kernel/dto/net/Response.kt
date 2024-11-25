package com.mercer.kernel.dto.net

/**
 * @author :Mercer
 * @Created on 2024/06/22.
 * @Description:
 *   网络响应
 */
interface Response<out T> where T : Any {

    // 响应码
    val code: Int

    // 响应信息
    val message: String?

    // 响应数据主体
    val data: T?

    // 是否成功
    val succeed: Boolean

}