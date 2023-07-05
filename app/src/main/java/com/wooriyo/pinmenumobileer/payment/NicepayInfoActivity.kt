package com.wooriyo.pinmenumobileer.payment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wooriyo.pinmenumobileer.databinding.ActivityNicepayInfoBinding

class NicepayInfoActivity : AppCompatActivity() {
    lateinit var binding: ActivityNicepayInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNicepayInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}