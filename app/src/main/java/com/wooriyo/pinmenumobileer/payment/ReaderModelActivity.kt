package com.wooriyo.pinmenumobileer.payment

import android.os.Bundle
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.databinding.ActivityReaderModelBinding

class ReaderModelActivity : BaseActivity() {
    lateinit var binding: ActivityReaderModelBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReaderModelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener { finish() }
    }
}