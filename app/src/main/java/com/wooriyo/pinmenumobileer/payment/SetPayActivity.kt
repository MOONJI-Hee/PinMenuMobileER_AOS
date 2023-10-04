package com.wooriyo.pinmenumobileer.payment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.common.AlertDialog
import com.wooriyo.pinmenumobileer.common.SelectStoreActivity
import com.wooriyo.pinmenumobileer.databinding.ActivitySetPayBinding
import com.wooriyo.pinmenumobileer.model.PaySettingDTO
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.more.MoreActivity
import com.wooriyo.pinmenumobileer.printer.PrinterMenuActivity
import com.wooriyo.pinmenumobileer.store.StoreListActivity
import com.wooriyo.pinmenumobileer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Query

class SetPayActivity : BaseActivity() {
    lateinit var binding: ActivitySetPayBinding

    val mActivity = this@SetPayActivity
    val TAG = "SetPayActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetPayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getPayInfo()

        binding.back.setOnClickListener { finish() }

        binding.usableDevice.setOnClickListener { startActivity(Intent(mActivity, ReaderModelActivity::class.java)) }

        binding.infoQR.setOnClickListener{
            AlertDialog(getString(R.string.use_post_QR), getString(R.string.use_post_QR_info), 1).show(supportFragmentManager, "AlertDialog")
        }
        binding.infoCard.setOnClickListener{
            AlertDialog(getString(R.string.use_post_card), getString(R.string.use_post_card_info), 1).show(supportFragmentManager, "AlertDialog")
        }

        //하단 네비게이션바 (추후 프레그먼트로 변경 예정)
        binding.icMain.setOnClickListener {
            MyApplication.setStoreDTO()
            MyApplication.storeidx = 0
            MyApplication.bidx = 0
            startActivity(Intent(mActivity, StoreListActivity::class.java))
        }
        binding.icPay.setOnClickListener {}
        binding.icPrinter.setOnClickListener {
            MyApplication.setStoreDTO()
            MyApplication.storeidx = 0
            MyApplication.bidx = 0
            when(MyApplication.storeList.size) {
                1 -> { insPrintSetting() }
                else -> {
                    val intent = Intent(mActivity, SelectStoreActivity::class.java)
                    intent.putExtra("type", "printer")
                    startActivity(intent)
                }
            }
        }
        binding.icMore.setOnClickListener { startActivity(Intent(mActivity, MoreActivity::class.java)) }
    }

    fun setView(settingDTO: PaySettingDTO) {
        binding.ckPostQR.isChecked = settingDTO.qrbuse == "Y"
        binding.ckPostCard.isChecked = settingDTO.cardbuse == "Y"

        val stts = if(settingDTO.mid.isNotEmpty() && settingDTO.mid_key.isNotEmpty()) "사용가능" else "연결전"
        binding.statusQR.text = String.format(getString(R.string.payment_status), stts)

        binding.setQR.setOnClickListener {
            if(settingDTO.mid.isEmpty() || settingDTO.mid_key.isEmpty()) {
                startActivity(Intent(mActivity, NicepayInfoActivity::class.java))
            }else{
                //수정 필요
                val intent = Intent(mActivity, SetPgInfoActivity::class.java)
                startActivity(intent)
            }
        }

        // 처음 진입했을 때는 이벤트 발생하지 않도록 위치 조정
        binding.ckPostQR.setOnCheckedChangeListener { _, _ -> udtPaySetting() }
        binding.ckPostCard.setOnCheckedChangeListener { _, _ -> udtPaySetting() }
    }

    fun getPayInfo() {
        ApiClient.service.getPayInfo(MyApplication.useridx, MyApplication.storeidx, MyApplication.androidId)
            .enqueue(object: Callback<PaySettingDTO>{
                override fun onResponse(call: Call<PaySettingDTO>, response: Response<PaySettingDTO>) {
                    Log.d(TAG, "결제 설정 조회 URL : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return
                    when(result.status) {
                        1 -> setView(result)
                        else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PaySettingDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "결제 설정 조회 오류 >> $t")
                    Log.d(TAG, "결제 설정 내용 조회 오류 >> ${call.request()}")
                }
            })
    }

    fun udtPaySetting() {
        val checkQr = if(binding.ckPostQR.isChecked) "Y" else "N"
        val checkCard = if(binding.ckPostCard.isChecked) "Y" else "N"

        ApiClient.service.udtPaySettting(MyApplication.useridx, MyApplication.storeidx, MyApplication.androidId, MyApplication.bidx, checkQr,checkCard)
            .enqueue(object : Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "결제 설정 URL : $response")
                if(!response.isSuccessful) return
                val result = response.body() ?: return

                when(result.status) {
                    1 -> Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "결제 설정 오류 >> $t")
                Log.d(TAG, "결제 설정 오류 >> ${call.request()}")
            }
        })
    }

    fun insPrintSetting() {
        ApiClient.service.insPrintSetting(
            MyApplication.useridx, MyApplication.storeList[0].idx,
            MyApplication.androidId
        )
            .enqueue(object : retrofit2.Callback<ResultDTO>{
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "프린터 설정 최초 진입 시 row 추가 url : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return
                    if(result.status == 1){
                        MyApplication.store = MyApplication.storeList[0]
                        MyApplication.storeidx = MyApplication.storeList[0].idx
                        MyApplication.bidx = result.bidx
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