package com.wooriyo.pinmenumobileer.printer

import android.bluetooth.BluetoothDevice
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.sam4s.io.ethernet.SocketInfo
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.MyApplication.Companion.androidId
import com.wooriyo.pinmenumobileer.MyApplication.Companion.bidx
import com.wooriyo.pinmenumobileer.MyApplication.Companion.bluetoothAdapter
import com.wooriyo.pinmenumobileer.MyApplication.Companion.remoteDevices
import com.wooriyo.pinmenumobileer.MyApplication.Companion.storeidx
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
    val mActivity = this@DetailPrinterActivity
    val TAG = "DetailPrinterActivity"

//    lateinit var printer: PrintDTO
    var printer_sw : BluetoothDevice ?= null
    var printer_s4 : SocketInfo ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPrinterBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        printer = intent.getSerializableExtra("printer") as PrintDTO

        printer_sw =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                intent.getParcelableExtra("sewoo", BluetoothDevice::class.java)
            else
                intent.getParcelableExtra("sewoo")

        printer_s4 =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                intent.getParcelableExtra("sam4s", SocketInfo::class.java)
            else
                intent.getParcelableExtra("sam4s")

        var img = 0
        var model = ""
        if(printer_sw != null) {
            img = R.drawable.skl_ts400b
            model = getString(R.string.skl_ts400b)
        }else if (printer_s4 != null) {
            img = R.drawable.sam4s
            model = getString(R.string.gcube)
        }

        binding.ivPrinter.setImageResource(img)
        binding.model.text = model

        binding.back.setOnClickListener { finish() }
        binding.save.setOnClickListener { save() }
        binding.delete.setOnClickListener { delete() }
    }

    fun save() {
        val nick = binding.etNickPrinter.text.toString()
        ApiClient.service.setPrintNick(useridx, storeidx, androidId, nick, 2)
            .enqueue(object : Callback<ResultDTO> {
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "프린터 별명 설정 URL >> $response")
                    if (!response.isSuccessful) return

                    val result = response.body() ?: return
                    when (result.status) {
                        1 -> {
                            Toast.makeText(mActivity, R.string.complete, Toast.LENGTH_SHORT).show()
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

        ApiClient.service.delPrint(useridx, storeidx, androidId, bidx)
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