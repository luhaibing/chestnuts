package com.mercer.sdk.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mercer.sdk.ui.unify.OnCoroutineScopeUnify
import com.mercer.sdk.ui.unify.OnExtensionUnify
import com.mercer.sdk.ui.unify.OnLifecycleUnify
import com.mercer.sdk.ui.unify.OnRequireActivityUnify
import com.mercer.sdk.ui.unify.OnRequireContextUnify
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope

/**
 * author:  mercer
 * date:    2024/3/14 01:23
 * desc:
 *   基础 activity
 */
abstract class BaseActivity : AppCompatActivity(),
    OnRequireContextUnify,
    OnRequireActivityUnify,
    OnLifecycleUnify,
    OnCoroutineScopeUnify,
    OnExtensionUnify {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    ///////////////////////////////////////////// Unify /////////////////////////////////////////////

    override fun requireActivity(): Activity {
        return this
    }

    override fun requireContext(): Context {
        return requireActivity()
    }

    private val handler = CoroutineExceptionHandler { _, throwable ->
        handleException(throwable)
    }

    private val scope: CoroutineScope by lazy {
        val name = CoroutineName("${javaClass.simpleName}-CoroutineScope")
        CoroutineScope(lifecycleScope.coroutineContext + handler + name)
    }

    override fun requireCoroutineScope(): CoroutineScope {
        return scope
    }

    ///////////////////////////////////////////// Unify /////////////////////////////////////////////

    protected fun handleException(value: Throwable) {
        //
    }

}