package com.wooriyo.pinmenumobileer.menu

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenumobileer.MyApplication.Companion.useridx
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivityChangeSeqBinding
import com.wooriyo.pinmenumobileer.menu.adapter.CateSeqAdapter
import com.wooriyo.pinmenumobileer.model.CategoryDTO
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import com.wooriyo.pinmenumobileer.util.TouchHelperCallback
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangeCateSeqActivity : BaseActivity() {
    lateinit var binding: ActivityChangeSeqBinding

    lateinit var cateList: ArrayList<CategoryDTO>
    lateinit var cateAdapter: CateSeqAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeSeqBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cateList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getSerializableExtra("cateList", ArrayList::class.java) as ArrayList<CategoryDTO>
        else
            intent.getSerializableExtra("cateList") as ArrayList<CategoryDTO>

        cateAdapter = CateSeqAdapter(cateList)
        val touchHelperCallback = TouchHelperCallback(cateAdapter)
        val touchHelper = ItemTouchHelper(touchHelperCallback)
        touchHelper.attachToRecyclerView(binding.rvCate)

        binding.rvCate.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        binding.rvCate.adapter = cateAdapter

        binding.back.setOnClickListener { finish() }
        binding.save.setOnClickListener { save() }
    }

    fun save() {
        val JSON = JSONArray()
        cateList.forEach {
            val json = JSONObject()
            json.put("idx", it.idx)

            JSON.put(json)
        }

        ApiClient.service.udtCateSeq(useridx, storeidx, JSON.toString()).enqueue(object: Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "카테고리 순서 변경 url : $response")
                if(!response.isSuccessful) return
                val result = response.body() ?: return

                Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                if(result.status == 1) finish()
            }
            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "카테고리 순서 변경 오류 >> ${call.request()}")
                Log.d(TAG, "카테고리 순서 변경 오류 >> $t")
            }
        })
    }
}