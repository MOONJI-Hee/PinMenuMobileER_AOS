package com.wooriyo.pinmenumobileer.menu

import android.os.Bundle
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivityAddOptBinding

class AddOptActivity : BaseActivity() {
    lateinit var binding: ActivityAddOptBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddOptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val req = intent.getIntExtra("optreq", 0)

        if(req == 0) {
            binding.title.text = resources.getString(R.string.option_choice)
        }

        binding.back.setOnClickListener { finish() }

    }
}