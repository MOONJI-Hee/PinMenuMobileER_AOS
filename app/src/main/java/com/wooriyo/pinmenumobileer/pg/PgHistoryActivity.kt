package com.wooriyo.pinmenumobileer.pg

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenumobileer.MyApplication.Companion.useridx
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivityPgHistoryBinding
import com.wooriyo.pinmenumobileer.model.PgHistoryDTO
import com.wooriyo.pinmenumobileer.model.PgResultDTO
import com.wooriyo.pinmenumobileer.pg.adapter.PgHistoryAdapter
import com.wooriyo.pinmenumobileer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PgHistoryActivity : BaseActivity() {
    lateinit var binding: ActivityPgHistoryBinding
    val mActivity = this@PgHistoryActivity
    val TAG = "PgHistoryActivity"

    val pgHistoryList = ArrayList<PgHistoryDTO>()
    val pgHistoryAdapter = PgHistoryAdapter(pgHistoryList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPgHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvPgHistory.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        binding.rvPgHistory.adapter = pgHistoryAdapter

        binding.back.setOnClickListener {
            storeidx = 0
            finish()
        }
        binding.icSearch.setOnClickListener { search() }
    }

    override fun onResume() {
        super.onResume()
        getPgList()
    }

    fun search() {

    }

    fun getPgList() {
        ApiClient.service.getPgList(useridx, storeidx).enqueue(object: Callback<PgResultDTO>{
            override fun onResponse(call: Call<PgResultDTO>, response: Response<PgResultDTO>) {
                Log.d(TAG, "pg 결제내역 조회 url : $response")

                if(!response.isSuccessful) return

                val result = response.body() ?: return

                when(result.status) {
                    1 -> {
                        result.pgHistoryList.removeIf { it.title.isEmpty() }

                        pgHistoryList.clear()
                        pgHistoryList.addAll(result.pgHistoryList)
                        pgHistoryAdapter.notifyDataSetChanged()
                    }
                    else -> {
                        Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<PgResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "pg 결제내역 조회 실패 > $t")
                Log.d(TAG, "pg 결제내역 조회 실패 > ${call.request()}")
            }
        })
    }
}