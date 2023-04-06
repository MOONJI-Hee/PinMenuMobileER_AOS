package com.wooriyo.pinmenumobileer.member

import android.os.Bundle
import android.widget.Toast
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivityFindPwBinding

class FindPwdActivity : BaseActivity() {
    lateinit var binding: ActivityFindPwBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindPwBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener { finish() }
        binding.send.setOnClickListener { findPw() }
    }

    fun findPw() {
        val email = binding.etEmail.text.toString()

        if (email.isEmpty()) {
            Toast.makeText(this@FindPwdActivity, R.string.email_hint, Toast.LENGTH_SHORT).show()
            return
        }
    }
}