package com.wooriyo.pinmenumobileer.member

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.wooriyo.pinmenumobileer.model.MemberDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.MyApplication.Companion.androidId
import com.wooriyo.pinmenumobileer.MyApplication.Companion.pref
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.store.StoreListActivity
import retrofit2.Call
import retrofit2.Response

class StartActivity : BaseActivity() {
    val TAG = "StartActivity"
    val mActivity = this@StartActivity

    var id = ""
    var pw = ""
    var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val memberDTO = pref.getMbrDTO()

        if(memberDTO == null) {
            startActivity(Intent(mActivity, LoginActivity::class.java).also {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } else {
            id = memberDTO.userid
            pw = pref.getPw().toString()
            token = pref.getToken().toString()
            loginWithApi()
        }

        Log.d(TAG, "Android ID >>> $androidId")
    }
    fun loginWithApi()  {
        ApiClient.service.checkMbr(id, pw, token, MyApplication.os, MyApplication.osver, MyApplication.appver, MyApplication.md, androidId)
            .enqueue(object: retrofit2.Callback<MemberDTO>{
                override fun onResponse(call: Call<MemberDTO>, response: Response<MemberDTO>) {
                    Log.d(TAG, "자동 로그인 url : $response")
                    if(!response.isSuccessful) return
                    val memberDTO = response.body()

                    if(memberDTO != null) {
                        if(memberDTO.status == 1 ) {
                            pref.setMbrDTO(memberDTO)
                            pref.setUserIdx(memberDTO.useridx)

                            startActivity(Intent(mActivity, StoreListActivity::class.java))
                        }else {
                            Toast.makeText(mActivity, memberDTO.msg, Toast.LENGTH_SHORT).show()
                            // status != 1일 때 (아이디 비번 오류, 필수정보 부족 등) 로그인 화면으로 이동
                            startActivity(Intent(mActivity, LoginActivity::class.java).also {
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            })
                        }
                    }
                }
                override fun onFailure(call: Call<MemberDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "자동 로그인 실패 >> $t")
                    // 네트워트 문제로 로그인 실패했을 때 내장 DB와 비교해서 로그인
//                    loginWithDB()
                }
            })
    }
}