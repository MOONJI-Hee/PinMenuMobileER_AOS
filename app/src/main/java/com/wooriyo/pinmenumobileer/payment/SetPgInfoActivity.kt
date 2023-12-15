package com.wooriyo.pinmenumobileer.payment

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.MyApplication.Companion.store
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivitySetPgInfoBinding
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.order.dialog.SetNicepayDialog
import com.wooriyo.pinmenumobileer.payment.dialog.PgBlinkDialog
import com.wooriyo.pinmenumobileer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SetPgInfoActivity : BaseActivity() {
    lateinit var binding: ActivitySetPgInfoBinding
//    val mActivity = this@SetPgInfoActivity
//    val TAG = "SetPgInfoActivity"

    var fromOrder = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetPgInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fromOrder = intent.getStringExtra("fromOrder") ?: ""

        binding.run {
            etRegNum.setText(store.pg_snum)
            etStoreName.setText(store.pg_storenm)
            etOwnerName.setText(store.pg_ceo)
            etStoreTel.setText(store.pg_tel)
            etStoreAddr.setText(store.pg_addr)
        }

        binding.back.setOnClickListener { finish() }
        binding.save.setOnClickListener { save() }
    }

    fun save() {
        val regNumber = binding.etRegNum.text.toString().trim()
        val storeNm = binding.etStoreName.text.toString().trim()
        val ownerNm = binding.etOwnerName.text.toString().trim()
        val storeTel = binding.etStoreTel.text.toString().trim()
        val storeAddr = binding.etStoreAddr.text.toString().trim()

        if(regNumber.isEmpty() || storeNm.isEmpty() || ownerNm.isEmpty() ||
            storeTel.isEmpty() || storeAddr.isEmpty()) {
            PgBlinkDialog().show(supportFragmentManager, "PgBlinkDialog")
        }else {
            ApiClient.service.insPgInfo(MyApplication.useridx, MyApplication.storeidx, regNumber, storeNm, ownerNm, storeTel, storeAddr)
                .enqueue(object : Callback<ResultDTO>{
                    override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                        Log.d(TAG, "PG 카드 심사 정보 입력 url : $response")
                        if(!response.isSuccessful) return

                        val result = response.body() ?: return
                        when(result.status) {
                            1 -> {
                                store.pg_snum = regNumber
                                store.pg_storenm = storeNm
                                store.pg_ceo = ownerNm
                                store.pg_tel = storeTel
                                store.pg_addr = storeAddr

                                if(fromOrder == "Y") {
                                    SetNicepayDialog().show(supportFragmentManager, "SetNicepayDialog")
                                }else {
                                    Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                            }
                            else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                        Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "PG 카드 심사 정보 입력 오류 >> $t")
                        Log.d(TAG, "PG 카드 심사 정보 입력 오류 >> ${call.request()}")
                    }
                })
        }
    }
}