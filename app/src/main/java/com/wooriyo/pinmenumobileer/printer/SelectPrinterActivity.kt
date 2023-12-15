package com.wooriyo.pinmenumobileer.printer

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.MyApplication.Companion.androidId
import com.wooriyo.pinmenumobileer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenumobileer.MyApplication.Companion.useridx
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivitySupportPrinterBinding
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectPrinterActivity : BaseActivity() {
    lateinit var binding: ActivitySupportPrinterBinding

//    val TAG = "SelectPrinterActivity"
//    val mActivity = this@SelectPrinterActivity

    var type = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupportPrinterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.run {
            // SupportPrinterActivity와 Layout 같이 쓰기 때문에 뷰 변경해주기
            title.text = getString(R.string.printer_title_sel_model)
            ckTs400b.visibility = View.VISIBLE
            ckTe202.visibility = View.VISIBLE
            ckSam4s.visibility = View.VISIBLE
            select.visibility = View.VISIBLE
            supportInfo.visibility = View.GONE
            openTs400b.visibility = View.GONE
            openTe202.visibility = View.GONE
            openGcube.visibility = View.GONE

            ts400b.setOnClickListener {
                ckTs400b.isChecked = true
            }
            te202.setOnClickListener {
                ckTe202.isChecked = true
            }
            gcube.setOnClickListener {
                ckSam4s.isChecked = true
            }

            ckTs400b.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    ckTe202.isChecked = false
                    ckSam4s.isChecked = false
                }
            }

            ckTe202.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    ckTs400b.isChecked = false
                    ckSam4s.isChecked = false
                }
            }

            ckSam4s.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    ckTs400b.isChecked = false
                    ckTe202.isChecked = false
                }
            }

            select.setOnClickListener {
                when {
                    ckTs400b.isChecked -> type = 1
                    ckTe202.isChecked -> type = 2
                    ckSam4s.isChecked -> type = 3
                }
                setPrinterModel()
            }

            back.setOnClickListener { finish() }
        }
    }

    fun setPrinterModel() {
        ApiClient.service.udtPrintModel(useridx, storeidx, androidId, type, "N").enqueue(object : Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "프린터 모델 선택 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status) {
                    1 -> {
                        intent.putExtra("printType", type)
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "프린터 모델 선택 오류 >> $t")
                Log.d(TAG, "프린터 모델 선택 오류 >> ${call.request()}")
            }
        })
    }
}