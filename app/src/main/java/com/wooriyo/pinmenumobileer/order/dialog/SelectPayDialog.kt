package com.wooriyo.pinmenumobileer.order.dialog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.wooriyo.pinmenumobileer.BaseDialogFragment
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.DialogSelectPayBinding
import com.wooriyo.pinmenumobileer.model.PaySettingDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectPayDialog(val position: Int): BaseDialogFragment() {
    lateinit var binding: DialogSelectPayBinding

    val TAG = context.toString()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogSelectPayBinding.inflate(layoutInflater)

        getPayInfo()

        binding.btnQR.setOnClickListener {  }
        binding.btnCard.setOnClickListener {  }
        binding.btnComplete.setOnClickListener {  }

        binding.unableQR.setOnClickListener {  }
        binding.unableCard.setOnClickListener {  }

        return binding.root
    }

    fun setView(settingDTO: PaySettingDTO) {
        if(settingDTO.qrbuse == "N")
            binding.unableQR.visibility = View.VISIBLE

        if(settingDTO.cardbuse == "N")
            binding.unableCard.visibility = View.VISIBLE
    }

    fun getPayInfo() {
        ApiClient.service.getPayInfo(MyApplication.useridx, MyApplication.storeidx, MyApplication.androidId)
            .enqueue(object: Callback<PaySettingDTO> {
                override fun onResponse(call: Call<PaySettingDTO>, response: Response<PaySettingDTO>) {
                    Log.d(TAG, "결제 설정 조회 URL : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return
                    when(result.status) {
                        1 -> setView(result)
                        else -> Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PaySettingDTO>, t: Throwable) {
                    Toast.makeText(context, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "결제 설정 조회 오류 >> $t")
                    Log.d(TAG, "결제 설정 내용 조회 오류 >> ${call.request()}")
                }
            })
    }
}