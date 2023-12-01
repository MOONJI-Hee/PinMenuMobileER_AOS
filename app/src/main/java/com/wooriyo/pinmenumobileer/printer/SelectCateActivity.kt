package com.wooriyo.pinmenumobileer.printer

import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenumobileer.MyApplication.Companion.useridx
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.common.dialog.AlertDialog
import com.wooriyo.pinmenumobileer.databinding.ActivitySelectCateBinding
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.model.CateListDTO
import com.wooriyo.pinmenumobileer.model.CategoryDTO
import com.wooriyo.pinmenumobileer.printer.adapter.CateAdapter
import com.wooriyo.pinmenumobileer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectCateActivity : BaseActivity() {
    lateinit var binding : ActivitySelectCateBinding

    val TAG = "SelectCateActivity"
    val mActivity = this@SelectCateActivity

    val allCateList = ArrayList<CategoryDTO>()
    val cateAdapter = CateAdapter(allCateList)

    var cnt = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectCateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cateAdapter.setOnCheckClickListener(object : ItemClickListener{
            override fun onCheckClick(position: Int, v: CheckBox, isChecked: Boolean) {
                if(isChecked) {
                    if(cnt < 10) {
                        allCateList[position].checked = 1
                        cnt++
                        setTitle()
                    }else {
                        v.isChecked = false
                        AlertDialog("", getString(R.string.printer_category_info), 0).show(supportFragmentManager, "AlertDialog")
                    }
                }else {
                    allCateList[position].checked = 0
                    if(cnt > 0) {
                        cnt--
                        setTitle()
                    }
                }
            }
        })

        binding.rvCate.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        binding.rvCate.adapter = cateAdapter

        getCategory()

        setTitle()

        binding.back.setOnClickListener { finish() }
        binding.save.setOnClickListener { save() }
    }

    fun setTitle() {
        binding.title.text = getString(R.string.printer_title_category_set).format(cnt)
    }

    fun getCategory() {
        ApiClient.service.getCateList(useridx, storeidx)
            .enqueue(object: Callback<CateListDTO> {
                override fun onResponse(call: Call<CateListDTO>, response: Response<CateListDTO>) {
                    Log.d(TAG, "카테고리 조회 url : $response")
                    if(!response.isSuccessful) {return}
                    val result = response.body()
                    if(result != null) {
                        when(result.status) {
                            1 -> {
                                allCateList.addAll(result.cateList)
                                cateAdapter.notifyDataSetChanged()
                            }
                            else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<CateListDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "카테고리 조회 실패 > $t")
                    Log.d(TAG, "카테고리 조회 실패 > ${call.request()}")
                }
            })
    }

    fun save() {
        var strCate = ""
        allCateList.forEach {
            if(it.checked == 1) {
                if(strCate == "")
                    strCate = it.name
                else
                    strCate += ",${it.name}"
            }
        }

        intent.putExtra("strCate", strCate)
        setResult(RESULT_OK, intent)
        finish()
    }
}