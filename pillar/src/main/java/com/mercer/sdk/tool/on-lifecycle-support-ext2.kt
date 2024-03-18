package com.mskj.mercer.core.tool

import androidx.lifecycle.LifecycleCoroutineScope
import com.mskj.mercer.core.support.OnLifecycleSupport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect

fun <T : Any?, F : Flow<T>> OnLifecycleSupport.observeNullable(
    flow: F,
    turnOn: LifecycleCoroutineScope.(suspend () -> Unit) -> Unit = { block ->
        launchWhenResumed {
            block()
        }
    },
    body: suspend (T) -> Unit
) {
    requireLifecycleScope().turnOn {
        flow.catch {

        }.collect {
            body(it)
        }
    }
}

fun <T : Any, F : Flow<T>> OnLifecycleSupport.observeNotNull(
    flow: F,
    turnOn: LifecycleCoroutineScope.(suspend () -> Unit) -> Unit = { block ->
        launchWhenResumed {
            block()
        }
    },
    body: suspend (T) -> Unit
) {
    requireLifecycleScope().turnOn {
        flow.catch {

        }.collect {
            body(it)
        }
    }
}

//fun <T : Any?, L : LiveData<T>> OnLifecycleSupport.observeForeverNullable(
//    liveData: L,
//    body: suspend (T) -> Unit
//) {
//    liveData.observeForever {
//        requireLifecycleScope().launch(coroutineExceptionHandler()) {
//            body(it)
//        }
//    }
//}
//
//fun <T : Any, L : LiveData<T>> OnLifecycleSupport.observeForeverNotNull(
//    liveData: L,
//    body: suspend (T) -> Unit
//) {
//    liveData.observeForever {
//        requireLifecycleScope().launch(coroutineExceptionHandler()) {
//            body(it)
//        }
//    }
//}

////////////////////////////////////////////////////////////////////////////////////////////////////

//fun <T : Any?, F : Flow<T>> OnLifecycleSupport.observeNullable(
//    flow: F,
//    turnOn: LifecycleCoroutineScope.(suspend () -> Unit) -> Unit = { block ->
//        launchWhenResumed {
//            block()
//        }
//    },
//    body: suspend (T) -> Unit
//) {
//    // 启动 开启
//    requireLifecycleScope().turnOn {
//        flow.collect {
//            body(it)
//        }
//    }
//}