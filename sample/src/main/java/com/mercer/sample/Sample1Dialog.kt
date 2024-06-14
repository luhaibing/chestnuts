package com.mercer.sample

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.mercer.paper.Paper
import com.mercer.paper.PaperContext
import com.mercer.paper.PaperCutter
import com.mercer.paper.context
import com.mercer.paper.elements.Character
import com.mercer.paper.elements.Material
import com.mercer.paper.elements.Page
import com.mercer.paper.elements.Tag
import com.mercer.sample.databinding.DialogSample1Binding

/**
 * author:  Mercer
 * date:    2024/6/15
 * desc:
 *   简单对话框1
 */
class Sample1Dialog(
    context: Context,
) : AlertDialog(context), Paper {

    override lateinit var paperContext: PaperContext

    private lateinit var binding: DialogSample1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogSample1Binding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        binding.tv.text = System.currentTimeMillis().toString()
    }

    companion object : Tag<Sample1Dialog>
    object T1 : Tag<Sample1Dialog>
    object T2 : Tag<Sample1Dialog>
    object M : Material<Sample1Dialog>

    object Producer : PaperCutter<Sample1Dialog> {
        override fun invoke(paperContext: PaperContext): Sample1Dialog {
            val context = paperContext.context()
            return Sample1Dialog(context).also {
                it.paperContext = paperContext
            }
        }
    }

}