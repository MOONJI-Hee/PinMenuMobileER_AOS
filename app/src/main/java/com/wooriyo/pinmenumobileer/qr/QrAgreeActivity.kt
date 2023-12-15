package com.wooriyo.pinmenumobileer.qr

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivityQrAgreeBinding
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.payment.SetPgInfoActivity
import com.wooriyo.pinmenumobileer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QrAgreeActivity : BaseActivity() {
    lateinit var binding: ActivityQrAgreeBinding
//    val mActivity = this@QrAgreeActivity
//    val TAG = "QrAgreeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrAgreeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(MyApplication.store.mid.isNullOrEmpty() || MyApplication.store.mid_key.isNullOrEmpty()) {
            binding.niceStatus.text = "연결전"
        }else {
            binding.niceStatus.text = "사용가능"
        }

        binding.run {
            back.setOnClickListener { finish() }
            // 수정 필요
            btnNicePay.setOnClickListener { startActivity(Intent(mActivity, SetPgInfoActivity::class.java)) }
            btnQrSet.setOnClickListener { goQrSetting() }
        }
    }

    fun goQrSetting() {
        if(binding.checkAgree.isChecked) {
            setNiceAgree()
        }else {
            Toast.makeText(mActivity, R.string.msg_do_agree, Toast.LENGTH_SHORT).show()
        }
    }

    fun setNiceAgree() {
        ApiClient.service.setNiceAgree(MyApplication.storeidx, MyApplication.androidId).enqueue(object: Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "이행보증보험 동의 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status) {
                    1 -> {
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "이행보증보험 동의 오류 >> $t")
                Log.d(TAG, "이행보증보험 동의 오류 >> ${call.request()}")
            }
        })
    }
}