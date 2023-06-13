package com.wooriyo.pinmenumobileer.printer

import android.os.Bundle
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.databinding.ActivityDetailPrinterBinding

class DetailPrinterActivity : BaseActivity() {
    lateinit var binding: ActivityDetailPrinterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPrinterBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}