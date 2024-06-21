package com.mercer.kernel.core

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.mercer.kernel.interfaces.HandledThrowable
import com.mercer.kernel.interfaces.HandledThrowableImpl
import com.mercer.kernel.interfaces.unify.OnRequireActivity
import com.mercer.kernel.interfaces.unify.OnRequireContext
import com.mercer.kernel.interfaces.unify.OnRequireLifecycle
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * author:  Mercer
 * date:    2024/6/22
 * desc:
 *   基础 Activity
 */

abstract class AbsActivity : AppCompatActivity(),
    OnRequireContext,
    OnRequireActivity,
    OnRequireLifecycle,
    HandledThrowable by HandledThrowableImpl() {

    ////////////////////////// Unify
    override fun requireActivity(): AppCompatActivity = this

    override fun requireLifecycleOwner(): LifecycleOwner = this

    private val handler = CoroutineExceptionHandler { _, throwable ->
        requireCoroutineScope().launch {
            handle(throwable)?.let { unhandledException(it) }
        }
    }

    private val scope: CoroutineScope by lazy {
        val name = CoroutineName("${javaClass.simpleName}-CoroutineScope")
        CoroutineScope(lifecycleScope.coroutineContext + handler + name)
    }

    override fun requireCoroutineScope(): CoroutineScope = scope

    ////////////////////////// Exception

    /**
     * 未处理的异常
     */
    protected open fun unhandledException(exception: Throwable) {
        exception.printStackTrace()
    }

    ////////////////////////// Effect

}