package com.mercer.sdk.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.withCreated
import androidx.lifecycle.withResumed
import androidx.lifecycle.withStarted
import com.mercer.sdk.ui.unify.OnCoroutineScopeUnify
import com.mercer.sdk.ui.unify.OnExtensionUnify
import com.mercer.sdk.ui.unify.OnLifecycleUnify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

/**
 * author:  mercer
 * date:    2024/3/14 01:24
 * desc:
 *   基础 fragment
 */
abstract class BaseFragment : Fragment(),
    OnLifecycleUnify,
    OnCoroutineScopeUnify,
    OnExtensionUnify {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val flow = flowOf(1)
        val liveData = MutableLiveData<Int>()

        liveData.observe(this) {}
        liveData.observe(viewLifecycleOwner) {}

        liveData.observe {}
        liveData.observeForever {  }


        requireCoroutineScope().launch {
            requireLifecycle().repeatOnLifecycle(Lifecycle.State.CREATED){

            }
            requireLifecycle().withCreated {  }
            requireLifecycle().withResumed {  }
            requireLifecycle().withStarted {  }
        }
        requireCoroutineScope().launch {

            flow.collect {}
            flow.collect()
            flow.collect()

        }


//
//        val owner: LifecycleOwner = this
    }


    ///////////////////////////////////////////// Unify /////////////////////////////////////////////

    override fun requireLifecycle(): Lifecycle {
        // TODO:
        return lifecycle
    }

    override fun requireLifecycleOwner(): LifecycleOwner {
        return viewLifecycleOwner
    }

    override fun requireCoroutineScope(): CoroutineScope {
        return lifecycleScope
    }

    ///////////////////////////////////////////// Unify /////////////////////////////////////////////

}