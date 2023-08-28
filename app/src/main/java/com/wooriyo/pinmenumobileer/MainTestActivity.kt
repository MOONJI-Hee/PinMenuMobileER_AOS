package com.wooriyo.pinmenumobileer

import android.os.Bundle
import com.wooriyo.pinmenumobileer.databinding.ActivityMainTestBinding

class MainTestActivity : BaseActivity() {
    lateinit var binding: ActivityMainTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}