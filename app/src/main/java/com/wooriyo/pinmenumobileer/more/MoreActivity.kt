package com.wooriyo.pinmenumobileer.more

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wooriyo.pinmenumobileer.databinding.ActivityMoreBinding

class MoreActivity : AppCompatActivity() {
    lateinit var binding: ActivityMoreBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}