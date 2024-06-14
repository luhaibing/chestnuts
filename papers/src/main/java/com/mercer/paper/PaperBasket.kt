package com.mercer.paper

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.mercer.paper.elements.ActivityAnchor
import com.mercer.paper.elements.Anchor
import com.mercer.paper.elements.Character
import com.mercer.paper.elements.Decal
import com.mercer.paper.elements.FragmentAnchor
import com.mercer.paper.elements.Level
import com.mercer.paper.elements.Lifecycle
import com.mercer.paper.elements.Material
import com.mercer.paper.elements.Page
import com.mercer.paper.elements.Tag


/**
 * author:  Mercer
 * date:    2024/6/15
 * desc:
 *   纸篓
 */
typealias Producer<E> = Pair<PaperContext, PaperCutter<E>>

class PaperBasket internal constructor(
    private val paperContext: PaperContext,
    private val cache: MutableMap<PaperContext, Paper> = hashMapOf(),
    private val producers: MutableMap<PaperContext, Producer<out Paper>> = hashMapOf(),
) : Fragment() {

    companion object {
        internal const val TAG = "Papers"

        private val DEFAULT_CONTEXT: PaperContext = PaperContext.Empty +
                Level.DEFAULT +
                Decal.DEFAULT

        private val PRODUCERS: MutableMap<PaperContext, Producer<out Paper>> = hashMapOf()

        /**
         * 注册
         */
        fun <P : Paper> registry(context: PaperContext, cutter: PaperCutter<P>) {
            val character = context[Character]
            val material = context[Material]
            if (character == null && material == null) {
                // Character 和 Material 不能全为空
                throw NullPointerException("Character and Material cannot both be empty.")
            }
            if (character != null && character !is Page) {
                val ctx = context.minusKey(Material)
                PRODUCERS[character] = ctx to cutter
            }
            if (material != null) {
                val ctx = context.minusKey(Character)
                PRODUCERS[material] = ctx to cutter
            }
        }
    }

    constructor(value: AppCompatActivity) :
            this(DEFAULT_CONTEXT + ActivityAnchor(value) + Lifecycle(value, value, value))

    constructor(value: Fragment) :
            this(DEFAULT_CONTEXT + FragmentAnchor(value) + Lifecycle(value, value, value))

    init {
        producers.putAll(PRODUCERS)
        paperContext[Lifecycle]!!.lifecycleOwner.lifecycle
            .addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Event) {
                }
            })
    }

    fun <P : Paper> registry(context: PaperContext, cutter: PaperCutter<P>) {
        val character = context[Character]
        val material = context[Material]
        if (character == null && material == null) {
            // Character 和 Material 不能全为空
            throw NullPointerException("Character and Material cannot both be empty.")
        }
        if (character is Tag) {
            val ctx = context.minusKey(Material) //+ Material.DEFAULT
            producers[character] = ctx to cutter
        }
        if (material != null) {
            val ctx = context.minusKey(Character)
            producers[material] = ctx to cutter
        }
    }


    /**
     * 创建实例
     */
    operator fun invoke(context: PaperContext): Paper? {
        val anchor = paperContext[Anchor]!!
        if (lifecycle.currentState == androidx.lifecycle.Lifecycle.State.INITIALIZED) {
            val transaction: FragmentTransaction = anchor.fragmentManager().beginTransaction()
            transaction.add(this, TAG)
            transaction.commitNow()
        }
        val character = context[Character]
        val material = context[Material]
        if (character == null && material == null) {
            // Character 和 Material 不能全为空
            throw NullPointerException("Character and Material cannot both be empty.")
        }
        if (character is Tag && character in producers) {
            if (character in cache) {
                return cache[character]
            }
            val (pContext, paperCutter) = producers[character]!!
            val ctx = (paperContext + pContext + context).minusKey(Material)
            val value = paperCutter(ctx)
            cache[character] = value
            return value
        }
        if (material != null && material in producers) {
            var page = character ?: Page.DEFAULT
            if (page == Page.INCREASE) {
                var offset = 1L
                do {
                    page = Page(offset)
                    offset++
                } while ((material + page) in cache)
            }
            val key = material + page
            if (key in cache) {
                return cache[key]
            }
            val (pContext, paperCutter) = producers[material]!!
            val ctx = paperContext + pContext + context
            val value = paperCutter(ctx)
            cache[key] = value
            return value
        }
        return null
    }

}