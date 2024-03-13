package com.mercer.sdk.viewmodel

import androidx.lifecycle.ViewModel
import com.mercer.sdk.ui.unify.OnCoroutineScopeUnify
import kotlinx.coroutines.CoroutineScope
import androidx.lifecycle.viewModelScope

/**
 * author:  mercer
 * date:    2024/3/14 02:10
 * desc:
 *   基础 ViewModel
 */
abstract class BaseViewModel : ViewModel(),
    OnCoroutineScopeUnify {

    ///////////////////////////////////////////// Unify /////////////////////////////////////////////

    override fun requireCoroutineScope(): CoroutineScope {
        return viewModelScope
    }

    ///////////////////////////////////////////// Unify /////////////////////////////////////////////



}