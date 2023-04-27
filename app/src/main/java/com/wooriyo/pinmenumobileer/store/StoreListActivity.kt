package com.wooriyo.pinmenumobileer.store

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenumobileer.model.StoreDTO
import com.wooriyo.pinmenumobileer.model.StoreListDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import com.wooriyo.pinmenumobileer.util.AppHelper
import com.wooriyo.pinmenumobileer.util.AppHelper.Companion.getRoundedCornerLT
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.MyApplication.Companion.density
import com.wooriyo.pinmenumobileer.MyApplication.Companion.pref
import com.wooriyo.pinmenumobileer.MyApplication.Companion.useridx
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivityStoreListBinding
import com.wooriyo.pinmenumobileer.more.MoreActivity
import com.wooriyo.pinmenumobileer.store.adapter.StoreAdapter
import retrofit2.Call
import retrofit2.Response

class StoreListActivity : BaseActivity() {
    lateinit var binding: ActivityStoreListBinding

    val TAG = "StoreActivity"
    val mActivity = this

    var storeList = ArrayList<StoreDTO>()
    var storeAdapter = StoreAdapter(storeList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        useridx = pref.getUserIdx()

        // 매장 리사이클러뷰, 어댑터 초기화
        binding.rvStore.layoutManager = LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false)
        binding.rvStore.adapter = storeAdapter

        // 매장리스트 배경 최소 높이 지정 (최소 화면을 덮을 정도로)
        binding.storeArea.minHeight = MyApplication.height

        // 매장리스트 조회
        getStoreList()

        // 우측 상단에 userid, 알파요 연동여부 출력
        val member = pref.getMbrDTO()
        if(member != null) {
            binding.userid.text = member.userid.substringBefore("@")

            if(member.arpayoid.isNullOrEmpty())
                binding.arpayo.text = getString(R.string.arpayo_dis_conn)
        }

        binding.icMain.setOnClickListener { startActivity(intent)}
        binding.icMore.setOnClickListener { startActivity(Intent(mActivity, MoreActivity::class.java)) }
    }

    override fun onResume() {
        super.onResume()
    }

    // 매장리스트 조회 Api
    fun getStoreList() {
        ApiClient.service.getStoreList(useridx)
            .enqueue(object: retrofit2.Callback<StoreListDTO>{
                override fun onResponse(call: Call<StoreListDTO>, response: Response<StoreListDTO>) {
                    Log.d(TAG, "매장 리스트 조회 url : $response")
                    if(response.isSuccessful) {
                        val storeListDTO = response.body() ?: return
                        if(storeListDTO.status == 1) {
                            storeList.clear()
                            storeList.addAll(storeListDTO.storeList)

                            if(storeList.isEmpty()) {
                                binding.empty.visibility = View.VISIBLE
                                binding.rvStore.visibility = View.GONE
                            }else {
                                binding.empty.visibility = View.GONE
                                binding.rvStore.visibility = View.VISIBLE
                                storeAdapter.notifyDataSetChanged()
                                AppHelper.setViewHeight(binding.rvStore, storeList.size, 200)
                            }
                        }else Toast.makeText(mActivity, storeListDTO.msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<StoreListDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "매장 리스트 조회 오류 > $t")
                    Log.d(TAG, "매장 리스트 조회 오류 > ${call.request()}")
                }
            })
    }
}