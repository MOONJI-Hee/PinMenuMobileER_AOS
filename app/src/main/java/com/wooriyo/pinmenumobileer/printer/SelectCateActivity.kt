package com.wooriyo.pinmenumobileer.printer

import android.os.Bundle
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.databinding.ActivitySelectCateBinding

class SelectCateActivity : BaseActivity() {
    lateinit var binding : ActivitySelectCateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectCateBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}