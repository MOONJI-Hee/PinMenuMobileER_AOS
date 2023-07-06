package com.wooriyo.pinmenumobileer.more

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.MyApplication.Companion.storeList
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.common.AlertDialog
import com.wooriyo.pinmenumobileer.common.ConfirmDialog
import com.wooriyo.pinmenumobileer.common.SelectStoreActivity
import com.wooriyo.pinmenumobileer.databinding.ActivityMoreBinding
import com.wooriyo.pinmenumobileer.member.LoginActivity
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.payment.SetPayActivity
import com.wooriyo.pinmenumobileer.printer.PrinterMenuActivity
import com.wooriyo.pinmenumobileer.store.StoreListActivity
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
            binding.back.setOnClickListener {
                val intent = Intent(mActivity, StoreListActivity::class.java)
                startActivity(intent)
            }
            changePwd.setOnClickListener { startActivity(Intent(mActivity, ChangePwdActivity::class.java)) }
            versionInfo.setOnClickListener {
                val content = getString(R.string.dialog_version).format(MyApplication.appver)
                AlertDialog("", content, 0).show(supportFragmentManager, "VersionDialog")
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

        binding.icMain.setOnClickListener { startActivity(intent)}
        binding.icPay.setOnClickListener {
            when(storeList.size) {
                0 -> Toast.makeText(mActivity, R.string.msg_no_store, Toast.LENGTH_SHORT).show()
                1 -> { insPaySetting() }
                else -> {
                    val intent = Intent(mActivity, SelectStoreActivity::class.java)
                    intent.putExtra("storeList", storeList)
                    intent.putExtra("type", "pay")
                    startActivity(intent)
                }
            }
        }
        binding.icPrinter.setOnClickListener {
            when(storeList.size) {
                0 -> Toast.makeText(mActivity, R.string.msg_no_store, Toast.LENGTH_SHORT).show()
                1 -> { insPrintSetting() }
                else -> {
                    val intent = Intent(mActivity, SelectStoreActivity::class.java)
                    intent.putExtra("storeList", storeList)
                    intent.putExtra("type", "printer")
                    startActivity(intent)
                }
            }
        }
        binding.icMore.setOnClickListener { startActivity(Intent(mActivity, MoreActivity::class.java)) }

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

    fun insPaySetting() {
        ApiClient.service.insPaySetting(MyApplication.useridx, storeList[0].idx, MyApplication.androidId)
            .enqueue(object : Callback<ResultDTO> {
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "결제 설정 최초 진입 시 row 추가 url : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return

                    if(result.status == 1) {
                        MyApplication.store = storeList[0]
                        MyApplication.storeidx = storeList[0].idx
                        MyApplication.bidx = result.bidx
                        startActivity(Intent(mActivity, SetPayActivity::class.java))
                    }else
                        Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "결제 설정 최초 진입 시 row 추가 오류 >> $t")
                    Log.d(TAG, "결제 설정 최초 진입 시 row 추가 오류 >> ${call.request()}")
                }
            })
    }

    fun insPrintSetting() {
        ApiClient.service.insPrintSetting(
            MyApplication.useridx, storeList[0].idx,
            MyApplication.androidId
        )
            .enqueue(object : retrofit2.Callback<ResultDTO>{
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "프린터 설정 최초 진입 시 row 추가 url : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return
                    if(result.status == 1){
                        MyApplication.store = storeList[0]
                        MyApplication.storeidx = storeList[0].idx
                        MyApplication.bidx = result.bidx
                        startActivity(Intent(mActivity, PrinterMenuActivity::class.java))
                    }else
                        Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "프린터 설정 최초 진입 시 row 추가 오류 >> $t")
                    Log.d(TAG, "프린터 설정 최초 진입 시 row 추가 오류 >> ${call.request()}")
                }
            })
    }
}