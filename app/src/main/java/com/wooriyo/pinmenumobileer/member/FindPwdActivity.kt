package com.wooriyo.pinmenumobileer.member

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivityFindPwBinding
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import com.wooriyo.pinmenumobileer.util.AppHelper.Companion.verifyEmail
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FindPwdActivity : BaseActivity() {
    lateinit var binding: ActivityFindPwBinding
//    val TAG = "FindPwdActivity"

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
        }else if (!verifyEmail(email)) {
            Toast.makeText(this@FindPwdActivity, R.string.msg_typemiss_id, Toast.LENGTH_SHORT).show()
        }else {
            ApiClient.service.findPwd(email).enqueue(object : Callback<ResultDTO> {
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "비밀번호 찾기 url : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return
                    if(result.status == 1){
                        Toast.makeText(this@FindPwdActivity, R.string.msg_find_pwd, Toast.LENGTH_SHORT).show()
                    }else
                        Toast.makeText(this@FindPwdActivity, result.msg, Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(this@FindPwdActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "비밀번호 찾기 실패 > $t")
                    Log.d(TAG, "비밀번호 찾기 실패 > ${call.request()}")
                }
            })
        }
    }
}