package com.wooriyo.pinmenumobileer.member

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MainActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivityMasterLoginBinding
import com.wooriyo.pinmenumobileer.model.MemberDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import com.wooriyo.pinmenumobileer.util.AppHelper
import retrofit2.Call
import retrofit2.Response

class MasterLoginActivity : BaseActivity() {
    lateinit var binding: ActivityMasterLoginBinding

    val mActivity = this@MasterLoginActivity
    val TAG = "MasterLoginActivity"

    var id = ""
    var pw = ""
    var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMasterLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.login.setOnClickListener{
            id = binding.etId.text.toString().trim()
            pw = binding.etPw.text.toString().trim()
            token = MyApplication.pref.getToken().toString()

            if(id.isEmpty()) {
                Toast.makeText(mActivity, R.string.msg_no_id, Toast.LENGTH_SHORT).show()
            }else if (pw.isEmpty()) {
                Toast.makeText(mActivity, R.string.msg_no_pw, Toast.LENGTH_SHORT).show()
            }else if(!AppHelper.verifyEmail(id)) {
                Toast.makeText(mActivity, R.string.msg_typemiss_id, Toast.LENGTH_SHORT).show()
            }else if(!AppHelper.verifyPw(pw)) {
                Toast.makeText(mActivity, R.string.msg_typemiss_pw, Toast.LENGTH_SHORT).show()
            }else {
                masterLogin()
            }
        }
    }
    fun masterLogin() {
        ApiClient.service.masterLogin(id, pw)
            .enqueue(object: retrofit2.Callback<MemberDTO>{
                override fun onResponse(call: Call<MemberDTO>, response: Response<MemberDTO>) {
                    Log.d(TAG, "마스터 로그인 url : $response")
                    if(!response.isSuccessful) return
                    val memberDTO = response.body()

                    if(memberDTO != null) {
                        if(memberDTO.status == 1 ) {
                            MyApplication.pref.setMbrDTO(memberDTO)
                            MyApplication.pref.setUserIdx(memberDTO.useridx)
                            MyApplication.pref.setPw(pw)
                            MyApplication.useridx = memberDTO.useridx

                            startActivity(Intent(mActivity, MainActivity::class.java))
                        }else {
                            Toast.makeText(mActivity, memberDTO.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<MemberDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "마스터 로그인 실패 >> $t")
                    Log.d(TAG, "마스터 로그인 실패 >> ${call.request()}")
                }
            })
    }
}