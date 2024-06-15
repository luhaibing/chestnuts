package com.mercer.kernel.interfaces.unify

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.withStateAtLeast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * author:  Mercer
 * date:    2024/6/22
 * desc:
 *   主要用于统一在 activity、fragment 中获取 Lifecycle 相关的方法
 */
interface OnRequireLifecycle : OnRequireCoroutineScope {

    override fun requireCoroutineScope(): CoroutineScope = requireLifecycleOwner().lifecycleScope

    ///////////////////////////////////////////////////

    fun requireLifecycleOwner(): LifecycleOwner

    fun requireLifecycle(): Lifecycle = requireLifecycleOwner().lifecycle


    ///////////////////////////////////////////////////

    /**
     * 订阅
     */
    fun <T> LiveData<T>.observe(block: suspend (T) -> Unit) {
        observe(requireLifecycleOwner()) {
            requireCoroutineScope().launch {
                block(it)
            }
        }
    }

    /**
     * 订阅
     */
    fun <T> LiveData<T>.observeForever(block: suspend (T) -> Unit) {
        observeForever {
            requireCoroutineScope().launch {
                block(it)
            }
        }
    }

    ///////////////////////////////////////////////////

    /**
     * 在指定生命周期订阅
     * 每当生命周期进入 (或高于) 目标状态时在一个新的协程中执行您作为参数传入的一个挂起块。
     * 如果生命周期低于目标状态，因执行该代码块而启动的协程就会被取消。
     */
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