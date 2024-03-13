package com.mercer.sdk.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mercer.sdk.ui.unify.OnContextUnify
import com.mercer.sdk.ui.unify.OnCoroutineScopeUnify
import com.mercer.sdk.ui.unify.OnExtensionUnify
import com.mercer.sdk.ui.unify.OnLifecycleUnify
import kotlinx.coroutines.CoroutineScope

/**
 * author:  mercer
 * date:    2024/3/14 01:23
 * desc:
 *   基础 activity
 */
abstract class BaseActivity : AppCompatActivity(),
    OnContextUnify,
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

    override fun requireCoroutineScope(): CoroutineScope {
        return lifecycleScope
    }

    ///////////////////////////////////////////// Unify /////////////////////////////////////////////


}