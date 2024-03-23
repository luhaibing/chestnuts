package com.mercer.pillar.interfaces.unify

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * author:  mercer
 * date:    2024/3/23 00:00
 * desc:
 *   主要用于统一在 activity、fragment 中获取 Lifecycle 相关的方法
 */
interface OnRequireLifecycle : OnRequireCoroutineScope {

    fun requireLifecycle(): Lifecycle

    fun requireLifecycleOwner(): LifecycleOwner

    ///////////////////////////////////////////////////

    /**
     * 订阅
     */
    fun <T> LiveData<T>.observe(block: (T) -> Unit) {
        observe(requireLifecycleOwner()) {
            requireCoroutineScope().launch {
                block(it)
            }
        }
    }

    /**
     * 订阅
     */
    fun <T> LiveData<T>.subscribe(block: (T) -> Unit) {
        observeForever {
            requireCoroutineScope().launch {
                block(it)
            }
        }
    }

    ///////////////////////////////////////////////////


    fun repeatOnLifecycle(state: Lifecycle.State, block: suspend CoroutineScope.() -> Unit) {
        requireCoroutineScope().launch {
            requireLifecycle().repeatOnLifecycle(state, block)
        }
    }

    fun repeatOnCreated(block: suspend CoroutineScope.() -> Unit) {
        repeatOnLifecycle(Lifecycle.State.CREATED, block)
    }

    fun repeatOnStarted(block: suspend CoroutineScope.() -> Unit) {
        repeatOnLifecycle(Lifecycle.State.STARTED, block)
    }

    fun repeatOnResumed(block: suspend CoroutineScope.() -> Unit) {
        repeatOnLifecycle(Lifecycle.State.RESUMED, block)
    }

}