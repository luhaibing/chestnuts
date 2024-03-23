package com.mercer.pillar.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mercer.pillar.interfaces.support.OnThrowableExport
import com.mercer.pillar.interfaces.support.OnThrowableHandle
import com.mercer.pillar.interfaces.unify.OnRequireCoroutineScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * author:  mercer
 * date:    2024/3/23 00:16
 * desc:
 *   基础 ViewModel
 */
abstract class BaseViewModel : ViewModel(),
    OnRequireCoroutineScope,
    OnThrowableHandle, OnThrowableExport {

    ///////////////////////////////////////////// Unify /////////////////////////////////////////////

    private val handler = CoroutineExceptionHandler { _, throwable ->
        handleThrowable(throwable)
    }

    private val scope: CoroutineScope by lazy {
        val name = CoroutineName("${javaClass.simpleName}-CoroutineScope")
        CoroutineScope(viewModelScope.coroutineContext + handler + name)
    }

    override fun requireCoroutineScope(): CoroutineScope {
        return scope
    }

    ///////////////////////////////////////////// Throwable /////////////////////////////////////////////

    protected val throwableFlow = MutableSharedFlow<Throwable>()

    override fun handleThrowable(value: Throwable) {
        throwableFlow.tryEmit(value)
    }

    override fun deliverThrowable(): Flow<Throwable> {
        return throwableFlow
    }

}