package com.mercer.paper.elements

import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import com.mercer.paper.PaperContext

/**
 * author:  Mercer
 * date:    2024/6/15
 * desc:
 *   生命周期相关
 */
data class Lifecycle(
    val lifecycleOwner: LifecycleOwner,
    val viewModelStoreOwner: ViewModelStoreOwner,
    val factory: HasDefaultViewModelProviderFactory
) : PaperContext.Element {
    companion object : PaperContext.Key<Lifecycle>
    override val key: PaperContext.Key<Lifecycle> = Lifecycle
}
