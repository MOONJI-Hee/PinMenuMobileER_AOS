package com.wooriyo.pinmenumobileer.printer

import android.content.Intent
import android.os.Bundle
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.databinding.ActivityPrinterMenuBinding
import com.wooriyo.pinmenumobileer.member.StartActivity

class PrinterMenuActivity : BaseActivity() {
    lateinit var binding: ActivityPrinterMenuBinding

    val TAG = this@PrinterMenuActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrinterMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.run {
            back.setOnClickListener { finish() }

            connSet.setOnClickListener { startActivity(Intent(TAG, SetConnActivity::class.java))}
            support.setOnClickListener { startActivity(Intent(TAG, SupportPrinterActivity::class.java)) }
            contentSet.setOnClickListener { startActivity(Intent(TAG, ContentSetActivity::class.java)) }
        }
    }
}