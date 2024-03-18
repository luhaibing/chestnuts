@file:Suppress("unused")

package com.mskj.mercer.core.tool

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mskj.mercer.core.R

/**
 * 默认加载时的占位图
 */
fun ImageView.load(
    url: Any?,
    placeholderRes: Int? = null,
    errorRes: Int? = null,
) {
    Glide.with(this)
        .load(url)
        //.transition(DrawableTransitionOptions.withCrossFade())
        .placeholder(placeholderRes ?: R.mipmap.ic_default_placeholder)
        .error(errorRes ?: R.mipmap.ic_default_placeholder)
        .into(this)
}

fun ImageView.loadRounded(
    url: Any?,
    corners: Number,
    placeholderRes: Int? = null,
    errorRes: Int? = null,
) {
    Glide.with(this)
        .load(url)
        // .transition(DrawableTransitionOptions.withCrossFade())
        .transform(RoundedCorners(corners.toInt()))
        .placeholder(placeholderRes ?: R.mipmap.ic_default_placeholder)
        .error(errorRes ?: R.mipmap.ic_default_placeholder)
        .into(this)
}

fun ImageView.loadCircle(
    url: Any?,
    placeholderRes: Int? = null,
    errorRes: Int? = null,
) {
    Glide.with(this)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade())
        .circleCrop()
        .placeholder(placeholderRes ?: R.mipmap.ic_default_placeholder)
        .error(errorRes ?: R.mipmap.ic_default_placeholder)
        .into(this)
}