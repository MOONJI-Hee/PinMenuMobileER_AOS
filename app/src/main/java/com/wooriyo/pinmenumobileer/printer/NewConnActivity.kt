package com.wooriyo.pinmenumobileer.printer

import android.os.Bundle
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.databinding.ActivityNewConnBinding

class NewConnActivity : BaseActivity() {
    lateinit var binding: ActivityNewConnBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewConnBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}