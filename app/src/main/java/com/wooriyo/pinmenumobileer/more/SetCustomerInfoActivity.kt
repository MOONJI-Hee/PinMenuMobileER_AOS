package com.wooriyo.pinmenumobileer.more

import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication.Companion.store
import com.wooriyo.pinmenumobileer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenumobileer.MyApplication.Companion.useridx
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivitySetCustomerInfoBinding
import com.wooriyo.pinmenumobileer.model.PgResultDTO
import com.wooriyo.pinmenumobileer.model.PgTableDTO
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.more.adapter.PgTableAdapter
import com.wooriyo.pinmenumobileer.util.ApiClient
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SetCustomerInfoActivity : BaseActivity() {
    lateinit var binding: ActivitySetCustomerInfoBinding
    val mActivity = this@SetCustomerInfoActivity
    val TAG = "SetCustomerInfoActivity"

    val tableList = ArrayList<PgTableDTO>()
    val tableAdapter = PgTableAdapter(tableList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetCustomerInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getTableList()

        binding.run {
            rvTable.layoutManager = LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false)
            rvTable.adapter = tableAdapter

            useName.isChecked = store.blname == "Y"
            usePhone.isChecked = store.blphone == "Y"
            useAddr.isChecked = store.bladdr == "Y"
            useEtc.isChecked = store.bletc == "Y"
            useNoti.isChecked = store.blmemo == "Y"

            if(store.memo.isNotEmpty())
                memo.setText(store.memo)

            back.setOnClickListener { finish() }
            save.setOnClickListener { save() }
        }
    }

    fun save() {
        val useName = checkUse(binding.useName)
        val usePhone = checkUse(binding.usePhone)
        val useAddr = checkUse(binding.useAddr)
        val useEtc = checkUse(binding.useEtc)
        val useNoti = checkUse(binding.useNoti)
        val useAll = checkUse(binding.allTable)
        val memo = binding.memo.text.toString()

        val jsonArray = JSONArray()

        if(useAll == "N") {
            tableList.forEach {
                val json = JSONObject()
                json.put("idx", it.idx)
                json.put("buse", it.buse)

                jsonArray.put(json)
            }
        }

        ApiClient.service.setQrCustomInfo(useridx, storeidx, useName, usePhone, useAddr, useEtc, useNoti, useAll, memo, jsonArray.toString()).enqueue(object : Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "결제고객 정보 설정 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status) {
                    1 -> {
                        Toast.makeText(mActivity, R.string.complete, Toast.LENGTH_SHORT).show()
                        store.blname = useName
                        store.blphone = usePhone
                        store.bladdr = useAddr
                        store.bletc = useEtc
                        store.blmemo = useNoti
                        store.memo = memo
                        finish()
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "결제고객 정보 설정 오류 > $t")
                Log.d(TAG, "결제고객 정보 설정 오류 > ${call.request()}")
            }
        })
    }

    fun getTableList() {
        ApiClient.service.getTableList(useridx, storeidx).enqueue(object:Callback<PgResultDTO>{
            override fun onResponse(call: Call<PgResultDTO>, response: Response<PgResultDTO>) {
                Log.d(TAG, "테이블 리스트 조회 URL : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status) {
                    1 -> {
                        tableList.clear()
                        tableList.addAll(result.tableList)
                        tableAdapter.checkAll(result.blAll)
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<PgResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "테이블 리스트 조회 오류 > $t")
                Log.d(TAG, "테이블 리스트 조회 오류 > ${call.request()}")
            }
        })
    }

    private fun checkUse(v: CheckBox): String {
        return if(v.isChecked) "Y" else "N"
    }
}