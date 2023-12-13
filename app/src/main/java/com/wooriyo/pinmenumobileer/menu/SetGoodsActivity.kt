package com.wooriyo.pinmenumobileer.menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivitySetGoodsBinding

class SetGoodsActivity : BaseActivity() {
    lateinit var binding: ActivitySetGoodsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetGoodsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}