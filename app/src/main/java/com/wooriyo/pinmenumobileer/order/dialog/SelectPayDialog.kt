package com.wooriyo.pinmenumobileer.order.dialog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.wooriyo.pinmenumobileer.BaseDialogFragment
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.DialogSelectPayBinding
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.model.PaySettingDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectPayDialog(val position: Int): BaseDialogFragment() {
    lateinit var binding: DialogSelectPayBinding

    lateinit var qrClickListener: ItemClickListener
    lateinit var cardClickListener: ItemClickListener
    lateinit var completeClickListener: ItemClickListener

    val TAG = context.toString()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogSelectPayBinding.inflate(layoutInflater)

        val fragmentActivity = context as FragmentActivity

        getPayInfo()

        binding.btnQR.setOnClickListener {
            qrClickListener.onItemClick(position)
        }
        binding.btnCard.setOnClickListener {
            cardClickListener.onItemClick(position)
        }
        binding.btnComplete.setOnClickListener {
            completeClickListener.onItemClick(position)
        }

        binding.unableQR.setOnClickListener {
            dismiss()
            NoPayDialog(0).show(fragmentActivity.supportFragmentManager, "NoQRDialog")
        }
        binding.unableCard.setOnClickListener {
            dismiss()
            NoPayDialog(1).show(fragmentActivity.supportFragmentManager, "NoCardDialog")
        }
        binding.cancel.setOnClickListener { dismiss() }

        return binding.root
    }

    fun setOnQrClickListener(qrClickListener: ItemClickListener) {
        this.qrClickListener = qrClickListener
    }

    fun setOnCardClickListener(cardClickListener: ItemClickListener) {
        this.cardClickListener = cardClickListener
    }

    fun setOnCompleteClickListener(completeClickListener: ItemClickListener) {
        this.completeClickListener = completeClickListener
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