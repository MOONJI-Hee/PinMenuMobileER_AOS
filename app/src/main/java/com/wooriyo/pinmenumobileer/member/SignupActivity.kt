package com.wooriyo.pinmenumobileer.member

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivitySignupBinding
import com.wooriyo.pinmenumobileer.model.MemberDTO
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.store.RegStoreActivity
import com.wooriyo.pinmenumobileer.util.ApiClient
import com.wooriyo.pinmenumobileer.util.AppHelper
import com.wooriyo.pinmenumobileer.util.AppHelper.Companion.verifyEmail
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class SignupActivity : BaseActivity() {
    lateinit var binding: ActivitySignupBinding
    val TAG = "SignUpActivity"
    val mActivity = this@SignupActivity

    var idChecked = false
    var arpaLinked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etId.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if(idChecked) {
                    idChecked = false
                    binding.checkResult.text = getString(R.string.unable)
                    binding.checkResult.setTextColor(Color.parseColor("#5A5A5A"))
                }
            }
        })

        binding.etArpayo.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if(arpaLinked) {
                    arpaLinked = false
                    binding.linkResult.text = getString(R.string.link_fail)
                    binding.linkResult.setTextColor(Color.parseColor("#5A5A5A"))
                }
            }
        })

        binding.back.setOnClickListener { finish() }
        binding.btnCheckId.setOnClickListener { checkID() }
        binding.btnArpayo.setOnClickListener { regArpayoId() }
        binding.save.setOnClickListener { save() }
    }

    private fun save() {
        val userid : String = binding.etId.text.toString()
        val pass : String = binding.etPwd.text.toString()
        val arpayoId = binding.etArpayo.text.toString()
        val token = MyApplication.pref.getToken() ?: ""
        val os = "A"
        val osvs = MyApplication.osver
        val appvs = MyApplication.appver
        val md = MyApplication.md

        if(userid.isEmpty() || userid == "") {
            Toast.makeText(mActivity, R.string.msg_no_id, Toast.LENGTH_SHORT).show()
            return
        }else if (!verifyEmail(userid)) {
            Toast.makeText(mActivity, R.string.msg_typemiss_id, Toast.LENGTH_SHORT).show()
            return
        }else if(!idChecked) {
            Toast.makeText(mActivity, R.string.msg_no_checked, Toast.LENGTH_SHORT).show()
            return
        }else if(pass.isEmpty() || pass == "") {
            Toast.makeText(mActivity, R.string.msg_no_pw, Toast.LENGTH_SHORT).show()
            return
        }else if(!AppHelper.verifyPw(pass)) {
            Toast.makeText(mActivity, R.string.msg_typemiss_pw, Toast.LENGTH_SHORT).show()
            return
        }else if ((arpayoId.isNotEmpty() && arpayoId != "") && !arpaLinked) {
            Toast.makeText(mActivity, R.string.msg_no_linked, Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.service.regMember(userid, arpayoId, pass, token, os, osvs, appvs, md)
            .enqueue(object : Callback<ResultDTO> {
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "회원가입 url : $response")
                    if(response.isSuccessful) {
                        val result = response.body() ?: return
                        if(result.status == 1) {
                            Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()

                            val useridx = result.idx
                            val memberDTO = MemberDTO(result.status, result.msg, useridx, userid, arpayoId)

                            MyApplication.pref.setMbrDTO(memberDTO)
                            MyApplication.pref.setUserIdx(memberDTO.useridx)
                            MyApplication.pref.setPw(pass)
                            MyApplication.useridx = useridx

                            startActivity(Intent(mActivity, RegStoreActivity::class.java))
                        }else {
                            Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "회원가입 오류 > $t")
                }
            })
    }

    // 아이디 중복체크
    fun checkID() {
        val userid = binding.etId.text.toString()

        if(userid.isEmpty() || userid == "") {
            Toast.makeText(mActivity, R.string.msg_no_id, Toast.LENGTH_SHORT).show()
            return
        }else if (!verifyEmail(userid)) {
            Toast.makeText(mActivity, R.string.msg_typemiss_id, Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.service.checkId(userid)
            .enqueue(object : Callback<ResultDTO> {
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "아이디 중복체크 url : $response")
                    if (!response.isSuccessful) return

                    val result = response.body()
                    if(result != null) {
                        if (result.status == 1) {
                            binding.checkResult.text = getString(R.string.able)
                            binding.checkResult.setTextColor(Color.parseColor("#FF6200"))
                            idChecked = true
                        } else {
                            Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                            binding.checkResult.text = getString(R.string.unable)
                            binding.checkResult.setTextColor(Color.parseColor("#5A5A5A"))
                            idChecked = false
                        }
                    }
                }
                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "아이디 중복체크 실패 > $t")
                    Log.d(TAG, "아이디 중복체크 실패 > ${call.request()}")
                }
            })
    }

    // 알파요 아이디 연동
    fun regArpayoId () {
        val arpayoId = binding.etArpayo.text.toString()

        if(arpayoId.isEmpty() || arpayoId == "") {
            Toast.makeText(mActivity, R.string.arpayo_id_hint, Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.service.checkArpayo(arpayoId)
            .enqueue(object : Callback<ResultDTO> {
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "알파요 아이디 연동 url : $response")
                    if (!response.isSuccessful) return

                    val result = response.body()
                    if(result != null) {
                        if (result.status == 1) {
                            binding.linkResult.text = getString(R.string.link_after)
                            binding.linkResult.setTextColor(Color.parseColor("#FF6200"))
                            arpaLinked = true
                        } else {
                            Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                            binding.linkResult.text = getString(R.string.link_fail)
                            binding.linkResult.setTextColor(Color.parseColor("#5A5A5A"))
                            arpaLinked = false
                        }
                    }
                }
                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "알파요 아이디 연동 실패 > $t")
                    Log.d(TAG, "알파요 아이디 연동 실패 > ${call.request()}")
                }
            })
    }
}