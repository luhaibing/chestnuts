package com.mercer.sample

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.mercer.paper.Paper
import com.mercer.paper.PaperContext
import com.mercer.paper.PaperCutter
import com.mercer.paper.anchor
import com.mercer.paper.elements.Tag
import com.mercer.sample.databinding.DialogSample1Binding
import com.mercer.sample.databinding.DialogSample2Binding

/**
 * author:  Mercer
 * date:    2024/6/15
 * desc:
 *   简单对话框2
 */
class Sample2Dialog : DialogFragment(), Paper {

    override lateinit var paperContext: PaperContext

    override fun show() {
        show(paperContext.anchor().fragmentManager(), "Sample2Dialog")
    }

    private lateinit var binding: DialogSample2Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DialogSample2Binding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("TAG","******* on ViewCreated *******")
    }
    companion object : Tag<Sample1Dialog>

    object Producer : PaperCutter<Sample2Dialog> {
        override fun invoke(paperContext: PaperContext): Sample2Dialog {
            return Sample2Dialog().also {
                it.paperContext = paperContext
            }
        }
    }
}