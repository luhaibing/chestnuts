package com.mercer.kernel.interfaces

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

/**
 * @author :Mercer
 * @Created on 2025/04/14.
 * @Description:
 *   生命周期支持
 */
interface OnLifecycleOwnerSupport : LifecycleOwner {

    fun handleLifecycleEvent(event: Lifecycle.Event)

}

class OnLifecycleOwnerSupportImpl : OnLifecycleOwnerSupport {

    override val lifecycle: LifecycleRegistry by lazy {
        LifecycleRegistry(this)
    }

    init {
        handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun handleLifecycleEvent(event: Lifecycle.Event) {
        lifecycle.handleLifecycleEvent(event)
    }

}