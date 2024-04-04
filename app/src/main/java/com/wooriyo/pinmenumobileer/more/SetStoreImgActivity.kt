package com.wooriyo.pinmenumobileer.more

import android.os.Bundle
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.databinding.ActivitySetStoreImgBinding

class SetStoreImgActivity : BaseActivity() {
    lateinit var binding: ActivitySetStoreImgBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetStoreImgBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}