package com.wooriyo.pinmenumobileer.menu

import android.os.Bundle
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.databinding.ActivityChangeSeqBinding

class ChangeSeqActivity : BaseActivity() {
    lateinit var binding: ActivityChangeSeqBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeSeqBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener { finish() }

    }
}