@file:Suppress("unused")

package com.mercer.paper

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import com.mercer.paper.elements.Anchor
import com.mercer.paper.elements.Decal
import com.mercer.paper.elements.Lifecycle

/**
 * author:  mercer
 * date:    2024/6/15
 * desc:
 *   扩展
 */
fun AppCompatActivity.papers(): Lazy<PaperBasket> {
    return PapersLazy(
        fragmentManager = supportFragmentManager,
        predicate = {
            PaperBasket(this)
        }
    )
}

fun Fragment.papers(): Lazy<PaperBasket> {
    return PapersLazy(
        fragmentManager = childFragmentManager,
        predicate = {
            PaperBasket(this)
        }
    )
}

fun Fragment.activityPapers(): Lazy<PaperBasket> {
    return PapersLazy(
        fragmentManager = requireActivity().supportFragmentManager,
        predicate = {
            PaperBasket(this)
        }
    )
}

internal class PapersLazy(
    private val fragmentManager: FragmentManager,
    private val predicate: () -> PaperBasket
) : Lazy<PaperBasket> {

    private var _value: PaperBasket? = null

    override val value: PaperBasket
        get() {
            if (_value == null) {
                _value = fragmentManager.findFragmentByTag(PaperBasket.TAG) as? PaperBasket
                if (_value == null) {
                    _value = predicate()
                }
            }
            return _value!!
        }

    override fun isInitialized(): Boolean {
        return _value == null
    }

}


inline fun <reified VM : ViewModel> Paper.anchorViewModel(
    noinline ownerProducer: (() -> ViewModelStoreOwner)? = null,
    noinline extrasProducer: (() -> CreationExtras)? = null,
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> {
    val lifecycle = paperContext[Lifecycle]
        ?: throw NullPointerException("Lifecycle can not be null.")
    val (_, viewModelStoreOwner, factory) = lifecycle
    val owner: ViewModelStoreOwner by lazy(LazyThreadSafetyMode.NONE) {
        ownerProducer?.invoke() ?: viewModelStoreOwner
    }
    return ViewModelLazy(
        viewModelClass = VM::class,
        storeProducer = {
            owner.viewModelStore
        },
        factoryProducer = factoryProducer ?: {
            (owner as? HasDefaultViewModelProviderFactory)?.defaultViewModelProviderFactory
                ?: factory.defaultViewModelProviderFactory
        },
        extrasProducer = {
            extrasProducer?.invoke()
                ?: (owner as? HasDefaultViewModelProviderFactory)?.defaultViewModelCreationExtras
                ?: CreationExtras.Empty
        }
    )
}

///////////////////////////////////////////////

operator fun PaperContext.contains(key: PaperContext.Key<*>): Boolean {
    return this[key] != null
}

fun PaperContext.anchor(): Anchor {
    return this[Anchor]!!
}

fun PaperContext.context(): Context {
    return anchor().context
}

/**
 * 获取参数
 */
inline fun <reified T> PaperContext.decal(): T? where T : Any {
    val decal = this[Decal]
    return decal?.value as? T
}