package com.wooriyo.pinmenumobileer.more

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.common.SelectStoreActivity
import com.wooriyo.pinmenumobileer.common.dialog.AlertDialog
import com.wooriyo.pinmenumobileer.common.dialog.ConfirmDialog
import com.wooriyo.pinmenumobileer.databinding.ActivitySetBinding
import com.wooriyo.pinmenumobileer.member.LoginActivity
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SetActivity : BaseActivity() {
    lateinit var binding: ActivitySetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        binding.run {
            back.setOnClickListener { finish() }
            qrCustomerInfo.setOnClickListener {
                when(MyApplication.storeList.size) {
                    0 -> Toast.makeText(mActivity, R.string.msg_no_store, Toast.LENGTH_SHORT).show()
                    1 -> {
                        if(MyApplication.storeList[0].paytype == 2) {
                            MyApplication.store = MyApplication.storeList[0]
                            MyApplication.storeidx = MyApplication.storeList[0].idx
                            startActivity(Intent(mActivity, SetCustomerInfoActivity::class.java))
                        }else {
                            AlertDialog("", getString(R.string.dialog_no_business)).show(supportFragmentManager, "NoBusinessDialog")
                        }
                    }
                    else ->  {
                        val intent = Intent(mActivity, SelectStoreActivity::class.java)
                        intent.putExtra("type", "customer_info")
                        startActivity(intent)
                    }
                }
            }
            udtMbr.setOnClickListener { startActivity(Intent(mActivity, MemberSetActivity::class.java)) }
            versionInfo.setOnClickListener {
                val content = getString(R.string.dialog_version).format(MyApplication.appver)
                AlertDialog("", content).show(supportFragmentManager, "VersionDialog")
            }
            logout.setOnClickListener {
                val onClickListener = View.OnClickListener {logout()}
                ConfirmDialog("", getString(R.string.dialog_logout), getString(R.string.btn_confirm), onClickListener).show(supportFragmentManager, "LogoutDialog")
            }
            drop.setOnClickListener {
                val onClickListener = View.OnClickListener {dropMbr()}
                ConfirmDialog("", getString(R.string.dialog_drop), getString(R.string.btn_confirm), onClickListener).show(supportFragmentManager, "DropDialog")
            }
        }
    }

    fun logout() {
        ApiClient.service.logout(MyApplication.useridx, MyApplication.pref.getToken().toString()).enqueue(object :
            Callback<ResultDTO> {
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "로그아웃 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status) {
                    1 -> {
                        MyApplication.pref.logout()
                        val intent = Intent(mActivity, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "로그아웃 실패 >> $t")
                Log.d(TAG, "로그아웃 실패 >> ${call.request()}")
            }
        })
    }

    fun dropMbr() {
        ApiClient.service.dropMbr(MyApplication.useridx).enqueue(object : Callback<ResultDTO> {
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