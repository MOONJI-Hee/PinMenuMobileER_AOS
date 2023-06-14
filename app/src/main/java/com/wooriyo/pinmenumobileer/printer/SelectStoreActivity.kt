package com.wooriyo.pinmenumobileer.printer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivitySelectStoreBinding
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.model.StoreDTO
import com.wooriyo.pinmenumobileer.more.MoreActivity
import com.wooriyo.pinmenumobileer.printer.adapter.StoreAdapter
import com.wooriyo.pinmenumobileer.store.StoreListActivity
import com.wooriyo.pinmenumobileer.util.ApiClient
import retrofit2.Call
import retrofit2.Response

class SelectStoreActivity : BaseActivity() {
    lateinit var binding: ActivitySelectStoreBinding

    val TAG = "SelectStoreActivity"
    val mActivity = this@SelectStoreActivity

    lateinit var storeList: ArrayList<StoreDTO>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storeList = intent.getSerializableExtra("storeList") as ArrayList<StoreDTO>

        val storeAdapter = StoreAdapter(storeList)
        storeAdapter.setOnItemClickListener(object : ItemClickListener{
            override fun onItemClick(position: Int) {
                super.onItemClick(position)
                insPrintSetting(position)
            }
        })

        binding.rvStore.layoutManager = LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false)
        binding.rvStore.adapter = storeAdapter

        binding.back.setOnClickListener { finish() }

        binding.icMain.setOnClickListener {
            startActivity(Intent(mActivity, StoreListActivity::class.java))
        }
        binding.icPrinter.setOnClickListener {}
        binding.icMore.setOnClickListener { startActivity(Intent(mActivity, MoreActivity::class.java)) }
    }

    fun insPrintSetting(position: Int) {
        ApiClient.service.insPrintSetting(MyApplication.useridx, MyApplication.storeidx, MyApplication.androidId)
            .enqueue(object : retrofit2.Callback<ResultDTO>{
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "프린터 설정 최초 진입 시 row 추가 url : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return

                    if(result.status == 1){
                        MyApplication.store = storeList[position]
                        MyApplication.storeidx = storeList[position].idx
                        MyApplication.bidx = result.idx
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