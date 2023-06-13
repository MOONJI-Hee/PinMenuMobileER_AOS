package com.wooriyo.pinmenumobileer.printer

import android.os.Bundle
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.databinding.ActivityContentSetBinding

class ContentSetActivity : BaseActivity() {
    lateinit var binding: ActivityContentSetBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContentSetBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}