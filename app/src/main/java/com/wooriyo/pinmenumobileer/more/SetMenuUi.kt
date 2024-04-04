package com.wooriyo.pinmenumobileer.more

import android.os.Bundle
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivitySetMenuUiBinding

class SetMenuUi : BaseActivity() {
    lateinit var binding: ActivitySetMenuUiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetMenuUiBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}