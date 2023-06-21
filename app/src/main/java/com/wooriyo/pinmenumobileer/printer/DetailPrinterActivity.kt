package com.wooriyo.pinmenumobileer.printer

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.MyApplication.Companion.androidId
import com.wooriyo.pinmenumobileer.MyApplication.Companion.useridx
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivityDetailPrinterBinding
import com.wooriyo.pinmenumobileer.model.PrintDTO
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailPrinterActivity : BaseActivity() {
    lateinit var binding: ActivityDetailPrinterBinding

    lateinit var printer: PrintDTO

    val TAG = "DetailPrinterActivity"
    val mActivity = this@DetailPrinterActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPrinterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        printer = intent.getSerializableExtra("printer") as PrintDTO

        var img = 0
        when (printer.printType) {
            1 -> img = R.drawable.skl_ts400b
            2 -> img = R.drawable.skl_te202
            3 -> img = R.drawable.sam4s
        }

        binding.ivPrinter.setImageResource(img)
        binding.model.text = printer.model
        if (printer.nick != "")
            binding.etNickPrinter.setText(printer.nick)

        binding.back.setOnClickListener { finish() }
        binding.save.setOnClickListener { save() }
        binding.delete.setOnClickListener { delete() }
    }

    fun save() {
        val nick = binding.etNickPrinter.text.toString()
        ApiClient.service.setPrintNick(useridx, MyApplication.storeidx, androidId, nick, 2)
            .enqueue(object : Callback<ResultDTO> {
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "프린터 별명 설정 URL >> $response")
                    if (!response.isSuccessful) return

                    val result = response.body() ?: return
                    when (result.status) {
                        1 -> {
                            Toast.makeText(mActivity, R.string.complete, Toast.LENGTH_SHORT).show()
                            printer.nick = nick
                        }
                        else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "프린터 별명 설정 오류 >> $t")
                    Log.d(TAG, "프린터 별명 설정 오류 >> ${call.request()}")
                }
            })
    }

    fun delete() {
        // 삭제 전 연결 해제
        if (MyApplication.bluetoothPort.isConnected) MyApplication.bluetoothPort.disconnect()

        ApiClient.service.delPrint(useridx, printer.storeidx, androidId, printer.idx)
            .enqueue(object : Callback<ResultDTO> {
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "프린터 삭제 URL : $response")
                    if (!response.isSuccessful) return

                    val result = response.body() ?: return
                    when (result.status) {
                        1 -> {
                            Toast.makeText(mActivity, R.string.complete, Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "프린터 삭제 오류 >> $t")
                    Log.d(TAG, "프린터 삭제 오류 >> ${call.request()}")
                }
            })
    }
}