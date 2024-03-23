package com.mercer.pillar.core

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mercer.pillar.interfaces.support.OnThrowableHandle
import com.mercer.pillar.interfaces.unify.OnRequireActivity
import com.mercer.pillar.interfaces.unify.OnRequireContext
import com.mercer.pillar.interfaces.unify.OnRequireLifecycle
import com.mercer.pillar.throwable.ExceptionHandler
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope

/**
 * author:  mercer
 * date:    2024/3/23 00:20
 * desc:
 *   基础 activity
 */
abstract class BaseActivity : AppCompatActivity(),
    OnRequireContext, OnRequireActivity,
    OnRequireLifecycle,
    OnThrowableHandle {

    ////////////////////////// Unify //////////////////////////

    override fun requireActivity(): Activity {
        return this
    }

    override fun requireContext(): Context {
        return requireActivity()
    }

    private val handler = CoroutineExceptionHandler { _, throwable ->
        handleThrowable(throwable)
    }

    private val scope: CoroutineScope by lazy {
        val name = CoroutineName("${javaClass.simpleName}-CoroutineScope")
        CoroutineScope(lifecycleScope.coroutineContext + handler + name)
    }

    override fun requireCoroutineScope(): CoroutineScope {
        return scope
    }

    ////////////////////////// Throwable //////////////////////////

    override fun handleThrowable(value: Throwable) {
        ExceptionHandler.handleThrowable(value)
    }


}