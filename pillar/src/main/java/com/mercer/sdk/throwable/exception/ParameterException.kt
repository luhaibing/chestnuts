package com.mskj.mercer.core.throwable.exception

import androidx.annotation.StringRes
import com.blankj.utilcode.util.StringUtils
import com.mskj.mercer.core.R

/**
 * 参数错误
 * 处理处理--> 打印提示信息
 */
class ParameterException(
    val key: String,
    val error: ParameterError = ParameterError.NULL
) : LocalException(error.conversion(key)) {

    constructor(
        @StringRes res: Int,
        error: ParameterError = ParameterError.NULL
    ) : this(StringUtils.getString(res))


    companion object {

        fun isNull(key: String) = ParameterException(key, ParameterError.NULL)
        fun isNull(@StringRes res: Int) = isNull(StringUtils.getString(res))

        fun error(key: String) = ParameterException(key, ParameterError.ERROR)
        fun error(@StringRes res: Int) = error(StringUtils.getString(res))

        fun canNotBeNull(key: String) = ParameterException(key, ParameterError.CAN_NOT_BE_NULL)
        fun canNotBeNull(@StringRes res: Int) = canNotBeNull(StringUtils.getString(res))

        fun norm(key: String) = ParameterException(key, ParameterError.NORM)
        fun norm(@StringRes res: Int) = norm(StringUtils.getString(res))

        fun choose(key: String) = ParameterException(key, ParameterError.CHOOSE)
        fun choose(@StringRes res: Int) = choose(StringUtils.getString(res))

    }

}

enum class ParameterError(
    val conversion: (String) -> String
) {

    // 填写有误
    ERROR({
        StringUtils.getString(R.string.tianxieyouwu_s, it)
    }),

    // 为空
    NULL({
        StringUtils.getString(R.string.weitianxie_s, it)
    }),

    // 不能为空
    CAN_NOT_BE_NULL({
        StringUtils.getString(R.string.s_bunengweikong, it)
    }),

    // 不规范
    NORM({
        StringUtils.getString(R.string.s_buguifan, it)
    }),

    // 请选择
    CHOOSE({
        StringUtils.getString(R.string.qingxuanze_s, it)
    })

}

// 有误
val PARAMETER_ERROR = ParameterError.ERROR

// 为空
val PARAMETER_IS_NULL = ParameterError.NULL

// 不能为空
val PARAMETER_CAN_NOT_BE_NULL = ParameterError.CAN_NOT_BE_NULL

// 不规范
val PARAMETER_NORM = ParameterError.NORM

