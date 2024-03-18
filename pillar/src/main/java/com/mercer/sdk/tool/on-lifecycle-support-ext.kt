package com.mskj.mercer.core.tool

import androidx.lifecycle.LiveData
import com.mskj.mercer.core.support.OnLifecycleSupport
import kotlinx.coroutines.launch

fun <T : Any?, L : LiveData<T>> OnLifecycleSupport.observeNullable(
    liveData: L,
    body: suspend (T) -> Unit
) {
    liveData.observe(requireLifecycleOwner()) {
        requireLifecycleScope().launch(coroutineExceptionHandler()) {
            body(it)
        }
    }
}

fun <T : Any, L : LiveData<T>> OnLifecycleSupport.observeNotNull(
    liveData: L,
    body: suspend (T) -> Unit
) {
    liveData.observe(requireLifecycleOwner()) {
        requireLifecycleScope().launch(coroutineExceptionHandler()) {
            if (it==null) {
                return@launch
            }
            body(it)
        }
    }
}

fun <T : Any?, L : LiveData<T>> OnLifecycleSupport.observeForeverNullable(
    liveData: L,
    body: suspend (T) -> Unit
) {
    liveData.observeForever {
        requireLifecycleScope().launch(coroutineExceptionHandler()) {
            body(it)
        }
    }
}

fun <T : Any, L : LiveData<T>> OnLifecycleSupport.observeForeverNotNull(
    liveData: L,
    body: suspend (T) -> Unit
) {
    liveData.observeForever {
        requireLifecycleScope().launch(coroutineExceptionHandler()) {
            if (it==null) {
                return@launch
            }
            body(it)
        }
    }
}