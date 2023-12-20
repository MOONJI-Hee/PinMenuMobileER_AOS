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
import com.wooriyo.pinmenumobileer.menu.adapter.GoodsAdapter
import com.wooriyo.pinmenumobileer.menu.adapter.GoodsSeqAdapter
import com.wooriyo.pinmenumobileer.model.CategoryDTO
import com.wooriyo.pinmenumobileer.model.GoodsDTO
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import com.wooriyo.pinmenumobileer.util.TouchHelperCallback
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangeMenuSeqActivity : BaseActivity() {
    lateinit var binding: ActivityChangeSeqBinding

    lateinit var goodsList: ArrayList<GoodsDTO>
    lateinit var goodsAdapter: GoodsSeqAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeSeqBinding.inflate(layoutInflater)
        setContentView(binding.root)

        goodsList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getSerializableExtra("goodsList", ArrayList::class.java) as ArrayList<GoodsDTO>
        else
            intent.getSerializableExtra("goodsList") as ArrayList<GoodsDTO>

        goodsAdapter = GoodsSeqAdapter(goodsList)
        val touchHelperCallback = TouchHelperCallback(goodsAdapter)
        val touchHelper = ItemTouchHelper(touchHelperCallback)
        touchHelper.attachToRecyclerView(binding.rvCate)

        binding.title.text = getText(R.string.menu_seq)
        binding.info.text = getText(R.string.menu_seq_change_way)

        binding.rvCate.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        binding.rvCate.adapter = goodsAdapter

        binding.back.setOnClickListener { finish() }
        binding.save.setOnClickListener { save() }
    }

    fun save() {
        val JSON = JSONArray()
        goodsList.forEach {
            val json = JSONObject()
            json.put("idx", it.idx)

            JSON.put(json)
        }

        ApiClient.service.udtGoodSeq(useridx, storeidx, JSON.toString()).enqueue(object : Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "메뉴 순서 변경 url : $response")
                if(!response.isSuccessful) return
                val result = response.body() ?: return

                Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                if(result.status == 1) finish()
            }
            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "메뉴 순서 변경 오류 >> ${call.request()}")
                Log.d(TAG, "메뉴 순서 변경 오류 >> $t")
            }
        })
    }
}