package com.wooriyo.pinmenumobileer.menu

import android.os.Bundle
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivitySetCategorySeqBinding

class SetCategorySeqActivity : BaseActivity() {
    lateinit var binding: ActivitySetCategorySeqBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetCategorySeqBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}