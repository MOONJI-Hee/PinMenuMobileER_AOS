package com.wooriyo.pinmenumobileer.qr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivityEventPreviewBinding

class EventPreviewActivity : AppCompatActivity() {
    lateinit var binding: ActivityEventPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}