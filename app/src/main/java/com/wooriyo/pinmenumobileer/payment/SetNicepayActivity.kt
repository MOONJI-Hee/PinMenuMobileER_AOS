package com.wooriyo.pinmenumobileer.payment

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivitySetNicepayBinding
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SetNicepayActivity : BaseActivity() {
    lateinit var binding: ActivitySetNicepayBinding

//    val TAG = "SetNicepayActivity"
//    val mActivity = this@SetNicepayActivity

    var fromOrder = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetNicepayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fromOrder = intent.getStringExtra("fromOrder") ?: ""

        val mid = intent.getStringExtra("mid")
        val mid_key = intent.getStringExtra("mid_key")

        if(mid != null) {
            binding.etMid.setText(mid)
        }

        if(mid_key != null) {
            binding.etKey.setText(mid_key)
        }

        binding.back.setOnClickListener { finish() }
        binding.save.setOnClickListener { save() }
    }

    fun save() {
        val mid = binding.etMid.text.toString()
        val key = binding.etKey.text.toString()

        if(mid.isEmpty()) {
            Toast.makeText(mActivity, R.string.msg_no_mid, Toast.LENGTH_SHORT).show()
            return
        }else if (key.isEmpty()) {
            Toast.makeText(mActivity, R.string.msg_no_key, Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.service.insMidSetting(MyApplication.useridx, MyApplication.storeidx, MyApplication.androidId, MyApplication.bidx, mid, key).enqueue(object : Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "나이스페이먼츠 key 설정 url : $response")
                if(!response.isSuccessful) return
                val result = response.body() ?: return

                when(result.status) {
                    1 -> {
                        if(fromOrder == "Y") {
//                            SetNicepayDialog().show(supportFragmentManager, "SetNicepayDialog")
                        }else
                            Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "나이스페이먼츠 key 설정 오류 >> $t")
                Log.d(TAG, "나이스페이먼츠 key 설정 오류 >> ${call.request()}")
            }
        })
    }
}