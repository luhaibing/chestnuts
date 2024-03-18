package com.mskj.mercer.core.tool

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.viewbinding.ViewBinding
import com.mskj.mercer.core.ui.ViewBindingActivity
import com.mskj.mercer.core.ui.CommonActivity
import com.mskj.mercer.core.ui.CommonFragment
import com.mskj.mercer.core.vm.VModel
import com.mskj.mercer.core.weidget.rv.CommonAdapter
import com.mskj.mercer.core.weidget.rv.CommonPagingDataAdapter
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

/**
 * 临时使用
 */
@Suppress("UNCHECKED_CAST", "", "unused")
object ReflectTool {

    /////////////////////////////// VModel
    @JvmStatic
    fun <VM : VModel> CommonActivity<*, VM>.viewModelInstance(): VM =
        viewModelInstance(this, this::class)

    @JvmStatic
    fun <VM : VModel> CommonFragment<*, VM>.viewModelInstance(share: Boolean): VM =
        viewModelInstance(this, this::class, share)

    private fun <VM : VModel> viewModelInstance(
        owner: ViewModelStoreOwner, zlass: KClass<*>, share: Boolean? = null,
    ): VM {
        val viewModelClass =
            (zlass.java.genericSuperclass as ParameterizedType).actualTypeArguments[1]
        if (share == true && owner is Fragment) {
            return ViewModelProvider(owner.requireActivity().viewModelStore,
                owner.requireActivity().defaultViewModelProviderFactory).get(viewModelClass as Class<VM>)
        }
        return ViewModelProvider(owner).get(viewModelClass as Class<VM>)
    }

    /////////////////////////////// ViewBinding

    fun <VB : ViewBinding> ViewBindingActivity<VB>.viewBindingInstance(
        position: Int, layoutInflater: LayoutInflater,
    ): VB {
        val viewBindingClass = (javaClass.genericSuperclass as ParameterizedType)
            .actualTypeArguments[position] as Class<*>
        val inflate = viewBindingClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
        val invoke = inflate.invoke(viewBindingClass, layoutInflater)
        return invoke as VB
    }

    fun <T : ViewBinding> Fragment.viewBindingInstance(
        position: Int, layoutInflater: LayoutInflater, container: ViewGroup?,
    ): T {
        val viewBindingClass =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[position] as Class<*>
        val inflate = viewBindingClass.getDeclaredMethod(
            "inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java
        )
        val invoke = inflate.invoke(viewBindingClass, layoutInflater, container, false)
        return invoke as T
    }

    fun <T : ViewBinding> CommonAdapter<*, T>.viewBindingInstance(
        position: Int, layoutInflater: LayoutInflater, container: ViewGroup?,
    ): T {
        val viewBindingClass =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[position] as Class<*>
        val inflate = viewBindingClass.getDeclaredMethod(
            "inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java
        )
        val invoke = inflate.invoke(viewBindingClass, layoutInflater, container, false)
        return invoke as T
    }

    fun <T : ViewBinding> CommonPagingDataAdapter<*, T>.viewBindingInstance(
        position: Int, layoutInflater: LayoutInflater, container: ViewGroup?,
    ): T {
        val viewBindingClass =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[position] as Class<*>
        val inflate = viewBindingClass.getDeclaredMethod(
            "inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java
        )
        val invoke = inflate.invoke(viewBindingClass, layoutInflater, container, false)
        return invoke as T
    }


}