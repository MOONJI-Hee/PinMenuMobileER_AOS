package com.wooriyo.pinmenumobileer.payment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.databinding.ActivityNicepayInfoBinding
import com.wooriyo.pinmenumobileer.databinding.ActivityNicepayJoinWayBinding
import com.wooriyo.pinmenumobileer.databinding.ActivityQrBinding

class NicepayJoinWayActivity : BaseActivity() {
    lateinit var binding: ActivityNicepayJoinWayBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNicepayJoinWayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.back.setOnClickListener { finish() }
    }
}