package com.mercer.pillar.throwable.exceptions

/**
 * author:  mercer
 * date:    2024/3/23 00:38
 * desc:
 *   本地异常,可直接将消息提示给用户查看
 */
open class LocalException(message: String?) : RuntimeException(message)
