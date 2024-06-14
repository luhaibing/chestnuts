package com.mercer.sample

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.mercer.paper.Paper
import com.mercer.paper.PaperContext
import com.mercer.paper.PaperCutter
import com.mercer.paper.context
import com.mercer.paper.elements.Material
import com.mercer.paper.elements.Tag
import com.mercer.sample.databinding.DialogSample1Binding
import com.mercer.sample.databinding.DialogSample3Binding

/**
 * author:  Mercer
 * date:    2024/6/15
 * desc:
 *   简单对话框3
 */
class Sample3Dialog(
    context: Context,
) : AlertDialog(context), Paper {

    override lateinit var paperContext: PaperContext

    private lateinit var binding: DialogSample3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogSample3Binding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        binding.tv.text = System.currentTimeMillis().toString()
    }

    companion object : Material<Sample3Dialog>

    object Producer : PaperCutter<Sample3Dialog> {
        override fun invoke(paperContext: PaperContext): Sample3Dialog {
            val context = paperContext.context()
            return Sample3Dialog(context).also {
                it.paperContext = paperContext
            }
        }
    }

}