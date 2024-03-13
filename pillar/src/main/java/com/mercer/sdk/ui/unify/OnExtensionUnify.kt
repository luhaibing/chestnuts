package com.mercer.sdk.ui.unify

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.withStateAtLeast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * author:  mercer
 * date:    2024/3/14 03:20
 * desc:
 *
 */
interface OnExtensionUnify : OnLifecycleUnify, OnCoroutineScopeUnify {

    /**
     * 订阅
     */
    fun <T> LiveData<T>.observe(block: (T) -> Unit) {
        observe(requireLifecycleOwner(), block)
    }

    /**
     * 订阅
     */
    fun <T> Flow<T>.collect(block: suspend (T) -> Unit) {
        requireCoroutineScope().launch {
            collect {
                block(it)
            }
        }
    }

    /**
     * 订阅
     */
    fun <T> Flow<T>.collect() {
        requireCoroutineScope().launch {
            collect()
        }
    }


    fun <T> repeatOnLifecycle(state: Lifecycle.State, block: suspend CoroutineScope.() -> Unit) {
        requireCoroutineScope().launch {
            requireLifecycle().repeatOnLifecycle(state, block)
        }
    }

    suspend fun <T> withStateAtLeast(state: Lifecycle.State, block: () -> T): T {
        return requireLifecycle().withStateAtLeast(state, block)
    }

}