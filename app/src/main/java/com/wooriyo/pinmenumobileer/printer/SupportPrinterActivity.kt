package com.wooriyo.pinmenumobileer.printer

import android.os.Bundle
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.databinding.ActivitySupportPrinterBinding

class SupportPrinterActivity : BaseActivity() {
    lateinit var binding: ActivitySupportPrinterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupportPrinterBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}