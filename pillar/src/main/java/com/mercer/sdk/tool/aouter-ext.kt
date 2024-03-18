package com.mskj.mercer.core.tool

import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.launcher.ARouter

fun route(path: String): Postcard = ARouter.getInstance().build(path)