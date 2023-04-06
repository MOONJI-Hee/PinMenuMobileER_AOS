package com.wooriyo.pinmenumobileer.call

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenumobileer.MyApplication.Companion.useridx
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.call.adapter.CallSetAdapter
import com.wooriyo.pinmenumobileer.databinding.ActivityOrderListBinding
import com.wooriyo.pinmenumobileer.model.CallDTO
import com.wooriyo.pinmenumobileer.model.CallSetListDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CallSetActivity : BaseActivity() {
    val TAG = "CallSetActivity"
    val mActivity = this@CallSetActivity
    lateinit var binding: ActivityOrderListBinding

    val setList = ArrayList<CallDTO>()
    private val callSetAdapter = CallSetAdapter(setList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // OrderList와 레이아웃 같이 쓰기 때문에, SetActivity에 맞게 뷰 변경

        binding.back.setOnClickListener{finish()}
//        binding.btnSet.setOnClickListener{finish()}

        setView()
        getCallList()
    }

    fun setView() {
        // 리사이클러뷰 초기화
        binding.rv.layoutManager = GridLayoutManager(mActivity, 4)
        binding.rv.adapter = callSetAdapter
    }

    private fun getCallList() {
        ApiClient.service.getCallList(useridx, storeidx)
            .enqueue(object : Callback<CallSetListDTO> {
                override fun onResponse(call: Call<CallSetListDTO>, response: Response<CallSetListDTO>) {
                    Log.d(TAG, "직원호출 전체 목록 조회 URL : $response")
                    if(!response.isSuccessful)
                        return

                    val callSetList = response.body()
                    if(callSetList != null) {
                        when(callSetList.status) {
                            1 -> {
                                setList.clear()
                                setList.addAll(callSetList.callList)

                                setList.add(CallDTO())

                                callSetAdapter.notifyDataSetChanged()
                            }
                            else -> Toast.makeText(mActivity, callSetList.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<CallSetListDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "직원호출 전체 목록 조회 오류 > $t")
                }
            })
    }
}