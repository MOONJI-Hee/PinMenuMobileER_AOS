package com.wooriyo.pinmenumobileer.printer

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivitySupportPrinterBinding
import com.wooriyo.pinmenumobileer.model.PrintModelDTO
import com.wooriyo.pinmenumobileer.model.PrintModelListDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SupportPrinterActivity : BaseActivity() {
    lateinit var binding: ActivitySupportPrinterBinding

    val TAG = "SupportPrinterActivity"
    val mActivity = this@SupportPrinterActivity

    val modelList = ArrayList<PrintModelDTO>()

    var opend_ts = false
    var opend_te = false
    var opend_cube = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupportPrinterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getSupportPrinter()

        binding.back.setOnClickListener { finish() }
        binding.ts400b.setOnClickListener {
            if(opend_ts) {
                binding.ts400bInfo.visibility = View.GONE
                binding.openTs400b.setImageResource(R.drawable.icon_list_arrow_down)
                opend_ts = false
            }else {
                binding.ts400bInfo.visibility = View.VISIBLE
                binding.openTs400b.setImageResource(R.drawable.icon_list_arrow_up)
                opend_ts = true
            }
        }
        binding.gcube.setOnClickListener {
            if(opend_cube) {
                binding.gcubeInfo.visibility = View.GONE
                binding.openGcube.setImageResource(R.drawable.icon_list_arrow_down)
                opend_cube = false
            }else {
                binding.gcubeInfo.visibility = View.VISIBLE
                binding.openGcube.setImageResource(R.drawable.icon_list_arrow_up)
                opend_cube = true
            }
        }
    }

    fun getSupportPrinter() {
        ApiClient.service.getSupportList().enqueue(object : Callback<PrintModelListDTO>{
            override fun onResponse(call: Call<PrintModelListDTO>, response: Response<PrintModelListDTO>) {
                Log.d(TAG, "연결 가능 프린터 리스트 조회 URL : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status) {
                    1 -> {
                        modelList.clear()
                        modelList.addAll(result.printList)
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PrintModelListDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "연결 가능 프린터 리스트 조회 오류 >> $t")
                Log.d(TAG, "연결 가능 프린터 리스트 조회 오류 >> ${call.request()}")
            }
        })
    }
}