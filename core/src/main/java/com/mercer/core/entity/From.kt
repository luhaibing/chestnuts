package com.mercer.core.entity

/**
 * @author :Mercer
 * @Created on 2024/11/28.
 * @Description:
 *   数据来源
 */
sealed interface From {
    /*
     * 缓存
     */
    data object Cache : From

    /*
     * 网络
     */
    data object Network : From
}