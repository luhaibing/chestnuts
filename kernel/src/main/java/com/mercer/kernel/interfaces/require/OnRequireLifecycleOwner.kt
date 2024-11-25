package com.mercer.kernel.interfaces.require

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.eventFlow
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.withStateAtLeast
import com.mercer.kernel.extension.withEvents
import com.mercer.kernel.extension.withStateAtLeast
import com.mercer.kernel.extension.withStates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * @author :Mercer
 * @Created on 2024/06/22.
 * @Description:
 *   主要用于统一在 activity、fragment 中获取 Lifecycle 相关的方法
 */
interface OnRequireLifecycleOwner : OnRequireCoroutineScope {

    fun requireLifecycleOwner(): LifecycleOwner

    fun requireLifecycle(): Lifecycle = requireLifecycleOwner().lifecycle

    ///////////////////////////////////////////////////

    /*
     * 在指定生命周期订阅
     * 每当生命周期进入 (或高于) 目标状态时在一个新的协程中执行您作为参数传入的一个挂起块。
     * 如果生命周期低于目标状态，因执行该代码块而启动的协程就会被取消。
     */
    fun repeatOnLifecycle(state: Lifecycle.State, block: suspend CoroutineScope.() -> Unit) {
        requireCoroutineScope().launch {
            requireLifecycle().repeatOnLifecycle(state, block)
        }
    }

    ///////////////////////////////////////////////////

    /*
     * 当生命周期进入 (或高于) 目标状态时在一个新的协程中执行您作为参数传入的一个挂起块。
     */
    suspend fun <R> withStateAtLeast(state: Lifecycle.State, block: () -> R): R {
        return requireLifecycle().withStateAtLeast(state) {
            block()
        }
    }

    ///////////////////////////////////////////////////

    /*
     * 当前生命周期变化的Flow
     */
    val currentStateFlow: StateFlow<Lifecycle.State>
        get() {
            return requireLifecycle().currentStateFlow
        }

    /*
     * 生命周期切换时事件切换的Flow
     */
    val eventFlow: Flow<Lifecycle.Event>
        get() {
            return requireLifecycle().eventFlow
        }

    ///////////////////////////////////////////////////

    override val coroutineScope: CoroutineScope
        get() {
            return requireLifecycleOwner().lifecycleScope
        }

    ///////////////////////////////////////////////////

    fun repeatOnCreated(block: suspend CoroutineScope.() -> Unit) {
        repeatOnLifecycle(Lifecycle.State.CREATED, block)
    }

    fun repeatOnStarted(block: suspend CoroutineScope.() -> Unit) {
        repeatOnLifecycle(Lifecycle.State.STARTED, block)
    }

    fun repeatOnResumed(block: suspend CoroutineScope.() -> Unit) {
        repeatOnLifecycle(Lifecycle.State.RESUMED, block)
    }

    ///////////////////////////////////////////////////

    suspend fun <R> withCreated(block: () -> R): R {
        return withStateAtLeast(Lifecycle.State.CREATED, block)
    }

    suspend fun <R> withStarted(block: () -> R): R {
        return withStateAtLeast(Lifecycle.State.STARTED, block)
    }

    suspend fun <R> withResumed(block: () -> R): R {
        return withStateAtLeast(Lifecycle.State.RESUMED, block)
    }

    ///////////////////////////////////////////////////

    fun <T : Any?> Flow<T>.withStateAtLeast(value: Lifecycle.State): Flow<T> {
        return withStateAtLeast(value, currentStateFlow)
    }

    fun <T : Any?> Flow<T>.withStateAtLeast(value: Lifecycle.Event): Flow<T> {
        return withStateAtLeast(value.targetState)
    }

    fun <T : Any?> Flow<T>.withStates(vararg values: Lifecycle.State): Flow<T> {
        return withStates(values = values, stateFlow = currentStateFlow)
    }

    fun <T : Any?> Flow<T>.withEvents(vararg values: Lifecycle.Event): Flow<T> {
        return withEvents(values = values, eventFlow = eventFlow)
    }

}