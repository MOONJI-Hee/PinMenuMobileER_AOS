package com.wooriyo.pinmenumobileer.printer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wooriyo.pinmenumobileer.databinding.ActivitySelectStoreBinding

class SelectStoreActivity : AppCompatActivity() {
    lateinit var binding: ActivitySelectStoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}