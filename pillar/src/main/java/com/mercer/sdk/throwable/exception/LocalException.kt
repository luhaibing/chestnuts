package com.mskj.mercer.core.throwable.exception

import androidx.annotation.StringRes
import com.blankj.utilcode.util.StringUtils
import com.mskj.mercer.core.support.OnResourceSupport
import com.mskj.mercer.core.support.impl.OnResourceSupportImpl

/**
 * 本地异常的超类
 */
open class LocalException(message: String?) : Exception(message ?: ""),
    OnResourceSupport by OnResourceSupportImpl() {

    constructor(@StringRes res: Int) : this(StringUtils.getString(res))

}