package com.wooriyo.pinmenumobileer.printer

import android.os.Bundle
import android.view.View
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.databinding.ActivitySupportPrinterBinding

class SelectPrinterActivity : BaseActivity() {
    lateinit var binding: ActivitySupportPrinterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupportPrinterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SupportPrinterActivity와 Layout 같이 쓰기 때문에 뷰 변경해주기
        binding.ckTs400b.visibility = View.VISIBLE
        binding.ckTe202.visibility = View.VISIBLE
        binding.ckSam4s.visibility = View.VISIBLE
        binding.select.visibility = View.VISIBLE
    }
}