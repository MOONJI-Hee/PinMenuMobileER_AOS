package com.wooriyo.pinmenumobileer.qr

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivityQrAgreeBinding
import com.wooriyo.pinmenumobileer.payment.SetNicepayActivity

class QrAgreeActivity : BaseActivity() {
    lateinit var binding: ActivityQrAgreeBinding
    val mActivity = this@QrAgreeActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrAgreeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.run {
            back.setOnClickListener { finish() }
            btnNicePay.setOnClickListener { startActivity(Intent(mActivity, SetNicepayActivity::class.java)) }
            btnQrSet.setOnClickListener { goQrSetting() }
        }
    }

    fun goQrSetting() {
        if(binding.checkAgree.isChecked) {
            setResult(RESULT_OK, intent)
            finish()
        }else {
            Toast.makeText(mActivity, R.string.msg_do_agree, Toast.LENGTH_SHORT).show()
        }
    }
}