package com.wooriyo.pinmenumobileer.payment

import android.os.Bundle
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.databinding.ActivitySetPayBinding

class SetPayActivity : BaseActivity() {
    lateinit var binding: ActivitySetPayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetPayBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}