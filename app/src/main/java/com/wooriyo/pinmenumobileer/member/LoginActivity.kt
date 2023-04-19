package com.wooriyo.pinmenumobileer.member

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.wooriyo.pinmenumobileer.model.MemberDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivityLoginBinding
import com.wooriyo.pinmenumobileer.store.StoreListActivity
import retrofit2.Call
import retrofit2.Response

class LoginActivity: BaseActivity() {
    lateinit var binding: ActivityLoginBinding

    val TAG = "LoginActivity"
    val mActivity = this@LoginActivity

    var waitTime = 0L
    var id = ""
    var pw = ""
    var token = ""

    // 뒤로가기 눌렀을 때 처리
    override fun onBackPressed() {
        if(System.currentTimeMillis() - waitTime > 2500) {
            waitTime = System.currentTimeMillis()
            Toast.makeText(this@LoginActivity, R.string.backpress, Toast.LENGTH_LONG).show()
        } else {
            finishAffinity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.login.setOnClickListener{
            id = binding.etId.text.toString().trim()
            pw = binding.etPw.text.toString().trim()
            token = MyApplication.pref.getToken().toString()
            when {
                id.isEmpty() -> Toast.makeText(this@LoginActivity, R.string.msg_no_id, Toast.LENGTH_SHORT).show()
                pw.isEmpty() -> Toast.makeText(this@LoginActivity, R.string.msg_no_pw, Toast.LENGTH_SHORT).show()
                else -> loginWithApi()
            }
        }

        binding.findPw.setOnClickListener{startActivity(Intent(mActivity, FindPwdActivity::class.java))}
    }

    fun loginWithApi()  {
        ApiClient.service.checkMbr(id, pw, token, MyApplication.os, MyApplication.osver, MyApplication.appver, MyApplication.md)
            .enqueue(object: retrofit2.Callback<MemberDTO>{
                override fun onResponse(call: Call<MemberDTO>, response: Response<MemberDTO>) {
                    Log.d(TAG, "로그인 url : $response")
                    if(!response.isSuccessful) return
                    val memberDTO = response.body()

                    if(memberDTO != null) {
                        if(memberDTO.status == 1 ) {
                            MyApplication.pref.setMbrDTO(memberDTO)
                            MyApplication.pref.setUserIdx(memberDTO.useridx)
                            MyApplication.pref.setPw(pw)

                            startActivity(Intent(mActivity, StoreListActivity::class.java))
                        }else {
                            Toast.makeText(this@LoginActivity, memberDTO.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<MemberDTO>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "로그인 실패 >> $t")
                    Log.d(TAG, "로그인 실패 >> ${call.request()}")
//                    loginWithDB()
                }
            })
    }

    fun loginWithDB () {

    }
}