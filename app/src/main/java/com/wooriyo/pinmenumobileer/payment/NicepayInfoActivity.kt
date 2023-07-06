package com.wooriyo.pinmenumobileer.payment

import android.content.Intent
import android.os.Bundle
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.databinding.ActivityNicepayInfoBinding

class NicepayInfoActivity : BaseActivity() {
    lateinit var binding: ActivityNicepayInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNicepayInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fromOrder = intent.getStringExtra("fromOrder") ?: ""

        binding.back.setOnClickListener { finish() }
        binding.joinWay.setOnClickListener { startActivity(Intent(this@NicepayInfoActivity, NicepayJoinWayActivity::class.java)) }
        binding.setting.setOnClickListener {
            val intent = Intent(this@NicepayInfoActivity, SetNicepayActivity::class.java)
            intent.putExtra("fromOrder", fromOrder)
            startActivity(intent)
        }
    }
}