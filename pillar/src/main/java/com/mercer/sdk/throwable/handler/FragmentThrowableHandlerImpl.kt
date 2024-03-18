package com.mskj.mercer.core.throwable.handler

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ToastUtils

/**
 * fragment不处理异常,统一转交给 Activity 去处理
 */
class FragmentThrowableHandlerImpl : OnThrowableHandler<Fragment> {

    private lateinit var fragment: Fragment

    override fun onAttach(target: Fragment) {
        fragment = target
    }

    private val activity: OnThrowableHandler<*> by lazy {
        fragment.requireActivity() as? AppCompatActivity as? OnThrowableHandler<*>?:object :OnThrowableHandler<Fragment>{

            override fun onAttach(target: Fragment) {

            }

            override fun onHandle(throwable: Throwable) {
                throwable.printStackTrace()
                onPrompt(throwable.message?:"")
            }

            override fun onPrompt(message: String) {
                ToastUtils.showShort(message)
            }
        }
    }

    override fun onHandle(throwable: Throwable) {
        activity.onHandle(throwable)
    }

    override fun onPrompt(message: String) {
        activity.onPrompt(message)
    }

}