package com.wooriyo.pinmenumobileer.payment.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.wooriyo.pinmenumobileer.MainActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.common.dialog.AlertDialog
import com.wooriyo.pinmenumobileer.databinding.FragmentSetPayBinding
import com.wooriyo.pinmenumobileer.model.PaySettingDTO
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.payment.NicepayInfoActivity
import com.wooriyo.pinmenumobileer.payment.ReaderModelActivity
import com.wooriyo.pinmenumobileer.payment.SetPgInfoActivity
import com.wooriyo.pinmenumobileer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SetPayFragment : Fragment() {
    lateinit var binding: FragmentSetPayBinding
    val TAG = "SetPayFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSetPayBinding.inflate(layoutInflater)

        getPayInfo()

        binding.usableDevice.setOnClickListener { startActivity(Intent(context, ReaderModelActivity::class.java)) }

        binding.infoQR.setOnClickListener{
            AlertDialog(getString(R.string.use_post_QR), getString(R.string.use_post_QR_info)).show((activity as MainActivity).supportFragmentManager, "AlertDialog")
        }
        binding.infoCard.setOnClickListener{
            AlertDialog(getString(R.string.use_post_card), getString(R.string.use_post_card_info)).show((activity as MainActivity).supportFragmentManager, "AlertDialog")
        }

        return binding.root
    }

    fun setView(settingDTO: PaySettingDTO) {
        binding.ckPostQR.isChecked = settingDTO.qrbuse == "Y"
        binding.ckPostCard.isChecked = settingDTO.cardbuse == "Y"

        val stts = if(settingDTO.mid.isNotEmpty() && settingDTO.mid_key.isNotEmpty()) "사용가능" else "연결전"
        binding.statusQR.text = String.format(getString(R.string.payment_status), stts)

        binding.setQR.setOnClickListener {
            if(settingDTO.mid.isEmpty() || settingDTO.mid_key.isEmpty()) {
                startActivity(Intent(context, NicepayInfoActivity::class.java))
            }else{
                //수정 필요
                val intent = Intent(context, SetPgInfoActivity::class.java)
                startActivity(intent)
            }
        }

        // 처음 진입했을 때는 이벤트 발생하지 않도록 위치 조정
        binding.ckPostQR.setOnCheckedChangeListener { _, _ -> udtPaySetting() }
        binding.ckPostCard.setOnCheckedChangeListener { _, _ -> udtPaySetting() }
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

    fun udtPaySetting() {
        val checkQr = if(binding.ckPostQR.isChecked) "Y" else "N"
        val checkCard = if(binding.ckPostCard.isChecked) "Y" else "N"

        ApiClient.service.udtPaySettting(MyApplication.useridx, MyApplication.storeidx, MyApplication.androidId, MyApplication.bidx, checkQr,checkCard)
            .enqueue(object : Callback<ResultDTO> {
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "결제 설정 URL : $response")
                    if(!response.isSuccessful) return
                    val result = response.body() ?: return

                    when(result.status) {
                        1 -> Toast.makeText(context, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(context, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "결제 설정 오류 >> $t")
                    Log.d(TAG, "결제 설정 오류 >> ${call.request()}")
                }
            })
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SetPayFragment().apply {
            }
    }
}