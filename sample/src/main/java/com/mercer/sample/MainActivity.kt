package com.mercer.sample

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mercer.paper.elements.Page
import com.mercer.paper.papers
import com.mercer.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.bt1.setOnClickListener {
            val paper = papers(Sample1Dialog)
            paper?.show()
        }
        binding.bt2.setOnClickListener {
            val paper = papers(Sample1Dialog)
            paper?.show()
        }
        binding.bt3.setOnClickListener {
            val paper = papers(Sample2Dialog)
            Log.e("TAG", "******* paper == $paper *******")
            paper?.show()
        }
        /////////
        binding.bt4.setOnClickListener {
            val paper = papers(Sample3Dialog + Page(0))
            Log.e("TAG", "******* paper == $paper *******")
            paper?.show()
        }
        binding.bt5.setOnClickListener {
            val paper = papers(Sample3Dialog + Page(1000))
            Log.e("TAG", "******* paper == $paper *******")
            paper?.show()
        }
        binding.bt6.setOnClickListener {
            val paper = papers(Sample3Dialog + Page.INCREASE)
            Log.e("TAG", "******* paper == $paper *******")
            paper?.show()
        }
        /////////
    }

    private val papers by papers()
}