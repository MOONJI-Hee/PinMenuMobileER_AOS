package com.wooriyo.pinmenumobileer.order

import android.os.Bundle
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.databinding.ActivityPayCardBinding

class PayCardActivity : BaseActivity() {
    lateinit var binding: ActivityPayCardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}