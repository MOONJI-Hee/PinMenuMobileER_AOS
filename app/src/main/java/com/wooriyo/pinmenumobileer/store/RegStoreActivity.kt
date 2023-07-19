package com.wooriyo.pinmenumobileer.store

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication.Companion.setStoreDTO
import com.wooriyo.pinmenumobileer.MyApplication.Companion.store
import com.wooriyo.pinmenumobileer.MyApplication.Companion.useridx
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.common.MapActivity
import com.wooriyo.pinmenumobileer.databinding.ActivityRegStoreBinding
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegStoreActivity : BaseActivity() {
    lateinit var binding: ActivityRegStoreBinding
    val mActivity = this@RegStoreActivity
    val TAG = "RegStoreActivity"

    val setAddr = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == RESULT_OK) {
            store.address = it.data?.getStringExtra("address").toString()
            store.long = it.data?.getStringExtra("long").toString()
            store.lat = it.data?.getStringExtra("lat").toString()

            Log.d(TAG, "매장 주소 >> ${store.address}")
            Log.d(TAG, "매장 경도 >> ${store.long}")
            Log.d(TAG, "매장 위도 >> ${store.lat}")

            if(store.address.isNotEmpty())
                binding.etAddr.setText(store.address)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStoreDTO()

        binding.save.setOnClickListener{ save() }
        binding.btnMap.setOnClickListener{
            val intent = Intent(mActivity, MapActivity::class.java)
            intent.putExtra("lat", store.lat) // 위도 (latitude)
            intent.putExtra("long", store.long) // 경도 (longitude)
            intent.putExtra("address", store.address)
            setAddr.launch(intent)
        }
    }

    fun save() {
        store.name = binding.etName.text.toString()
        store.address = binding.etAddr.text.toString()

        if(store.name.isEmpty()) {
            Toast.makeText(mActivity, R.string.store_name_hint, Toast.LENGTH_SHORT).show()
        }
//        else if (store.address.isEmpty()) {
//            Toast.makeText(mActivity, R.string.msg_no_addr, Toast.LENGTH_SHORT).show()
//        }
        else {
            ApiClient.service.regStore(useridx, store.name, store.address, store.long, store.lat)
                .enqueue(object : Callback<ResultDTO> {
                    override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                        Log.d(TAG, "매장 등록 url : $response")
                        val resultDTO = response.body()
                        if(resultDTO != null) {
                            if(resultDTO.status == 1) {
                                Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                                startActivity(Intent(mActivity, StoreListActivity::class.java))
                            }else {
                                Toast.makeText(mActivity, resultDTO.msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                        Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "매장 등록 실패 > $t")
                        Log.d(TAG, "매장 등록 실패 > ${call.request()}")
                    }
                })
        }
    }
}