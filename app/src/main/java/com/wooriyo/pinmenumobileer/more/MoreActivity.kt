package com.wooriyo.pinmenumobileer.more

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.common.ConfirmDialog
import com.wooriyo.pinmenumobileer.databinding.ActivityMoreBinding
import com.wooriyo.pinmenumobileer.member.LoginActivity
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MoreActivity : BaseActivity() {
    lateinit var binding: ActivityMoreBinding
    val mActivity = this@MoreActivity
    val TAG = "MoreActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.run {
            back.setOnClickListener { finish() }
            changePwd.setOnClickListener { startActivity(Intent(mActivity, ChangePwdActivity::class.java)) }
            versionInfo.setOnClickListener {  }
            logout.setOnClickListener {
                val onClickListener = View.OnClickListener {logout()}
                ConfirmDialog(getString(R.string.dialog_logout), getString(R.string.btn_confirm), onClickListener).show(supportFragmentManager, "LogoutDialog")
            }
            drop.setOnClickListener {
                val onClickListener = View.OnClickListener {dropMbr()}
                ConfirmDialog(getString(R.string.dialog_drop), getString(R.string.btn_confirm), onClickListener).show(supportFragmentManager, "DropDialog")
            }
        }
    }
    fun logout() {
        MyApplication.pref.logout()
        val intent = Intent(mActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    fun dropMbr() {
        ApiClient.service.dropMbr(MyApplication.useridx).enqueue(object : Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "회원 탈퇴 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status) {
                    1 -> {
                        Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                        logout()
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "회원 탈퇴 실패 >> $t")
                Log.d(TAG, "회원 탈퇴 실패 >> ${call.request()}")
            }
        })
    }
}