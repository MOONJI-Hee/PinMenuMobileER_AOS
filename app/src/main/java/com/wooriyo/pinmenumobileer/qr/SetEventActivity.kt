package com.wooriyo.pinmenumobileer.qr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivitySetEventBinding

class SetEventActivity : BaseActivity() {
    lateinit var binding: ActivitySetEventBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        
    }
}