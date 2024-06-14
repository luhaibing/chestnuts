package com.mercer.paper.elements

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.mercer.paper.PaperContext

/**
 * author:  Mercer
 * date:    2024/6/15
 * desc:
 *   锚点
 */
sealed class Anchor : PaperContext.Element {
    companion object : PaperContext.Key<Anchor>

    override val key: PaperContext.Key<Anchor> = Anchor
    abstract val value: Any
    abstract val context: Context
    abstract fun fragmentManager(): FragmentManager
}

data class ActivityAnchor(
    override val value: FragmentActivity
) : Anchor() {
    override val context: Context
        get() {
            return value
        }

    override fun fragmentManager(): FragmentManager {
        return value.supportFragmentManager
    }
}

data class FragmentAnchor(
    override val value: Fragment,
) : Anchor() {
    override val context: Context
        get() {
            return value.requireContext()
        }

    override fun fragmentManager(): FragmentManager {
        return value.childFragmentManager
    }
}