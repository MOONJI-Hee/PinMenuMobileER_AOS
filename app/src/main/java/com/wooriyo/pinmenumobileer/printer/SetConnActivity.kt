package com.wooriyo.pinmenumobileer.printer

import android.os.Bundle
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.databinding.ActivitySetConnBinding

class SetConnActivity : BaseActivity() {
    lateinit var binding: ActivitySetConnBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetConnBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}