package com.wooriyo.pinmenumobileer.printer

import android.bluetooth.BluetoothDevice
import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.sam4s.io.ethernet.SocketInfo
import com.sewoo.request.android.RequestHandler
import com.wooriyo.pinmenumobileer.broadcast.BtDiscoveryReceiver
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.MyApplication.Companion.androidId
import com.wooriyo.pinmenumobileer.MyApplication.Companion.bidx
import com.wooriyo.pinmenumobileer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenumobileer.MyApplication.Companion.useridx
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivityNewConnBinding
import com.wooriyo.pinmenumobileer.listener.DialogListener
import com.wooriyo.pinmenumobileer.model.PrintContentDTO
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.printer.dialog.SetNickDialog
import com.wooriyo.pinmenumobileer.printer.sam4s.NetworkPrinterInfo
import com.wooriyo.pinmenumobileer.util.ApiClient
import com.wooriyo.pinmenumobileer.util.AppHelper
import com.wooriyo.pinmenumobileer.util.AppHelper.Companion.searchDevice
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class NewConnActivity : BaseActivity() {
    lateinit var binding: ActivityNewConnBinding

    val TAG = "NewConnActivity"
    val mActivity = this@NewConnActivity

    var printerNick = ""
    var printerModel = ""
    var printType = 0

    // Broadcast Receiver
    val connectDevice = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            loadingDialog.dismiss()

            if (intent == null) return

            val action = intent.action
            if (BluetoothDevice.ACTION_ACL_CONNECTED == action) {
                setConnStatus()
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED == action) {
                setDisConnStatus()

                try {
                    if (MyApplication.bluetoothPort.isConnected) MyApplication.bluetoothPort.disconnect()
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                if (MyApplication.btThread != null) {
                    if(MyApplication.btThread!!.isAlive) {
                        MyApplication.btThread!!.interrupt()
                        MyApplication.btThread = null
                    }
                }
            }
        }
    }

    val discoveryResult = BtDiscoveryReceiver()

    private val choosePrinterModel = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == RESULT_OK) {
            loadingDialog.show(supportFragmentManager, "loading")
            val data = it.data ?: return@registerForActivityResult

            printType = data.getIntExtra("printType", 0)

            setPrintInfo()

            // 프린터 새로 등록 전에 이미 연결되어있으면 연결 끊기
            if (MyApplication.bluetoothPort.isConnected) {
                MyApplication.bluetoothPort.disconnect()
            }

            Thread(Runnable {
                if(printType == 3){
                    val list = AppHelper.searchCube(mActivity)

                    if(AppHelper.list != null && AppHelper.list!!.size > 0) {
                        Log.d("AppeHelper", "device 찾음")
                        Log.d("AppeHelper", "프린터 왜 정보 안나와.. >>>> ${(AppHelper.list!![0] as SocketInfo).address}")
                        Log.d("AppeHelper", "프린터 왜 정보 안나와.. >>>> ${(AppHelper.list!![0] as SocketInfo).port}")

                        Log.d("AppeHelper", "프린터 정보 >>>> ${NetworkPrinterInfo(AppHelper.list!![0] as SocketInfo).getTitle()}")
                        Log.d("AppeHelper", "프린터 정보 >>>> ${NetworkPrinterInfo(AppHelper.list!![0] as SocketInfo).getSubTitle()}")
                        Log.d("AppeHelper", "프린터 정보 >>>> ${NetworkPrinterInfo(AppHelper.list!![0] as SocketInfo)}")

//                    AppHelper.stopSearchCube()
                        AppHelper.connectCube(mActivity, list!![0] as SocketInfo)
                    }
                }else searchDevice()
            }).start()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewConnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // BroadCast Receiver
        registerReceiver(discoveryResult, IntentFilter(BluetoothDevice.ACTION_FOUND))
        registerReceiver(connectDevice, IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED))
        registerReceiver(connectDevice, IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED))

        getPrintSetting()

        binding.back.setOnClickListener { finish() }
        binding.nickDevice.setOnClickListener {
            showSetNickDialog(1)
        }
        binding.nickPrinter.setOnClickListener {
            showSetNickDialog(2)
        }
        binding.btnPlus.setOnClickListener {
            val intent = Intent(mActivity, SelectPrinterActivity::class.java)
            choosePrinterModel.launch(intent)
        }
        binding.btnRetry.setOnClickListener {
            val rtnVal = AppHelper.connDevice(0)

            if (rtnVal == 0) { // Connection success.
                val rh = RequestHandler()
                MyApplication.btThread = Thread(rh)
                MyApplication.btThread!!.start()

//                setPrintConnStatus("Y")
            } else // Connection failed.
                Toast.makeText(mActivity, "블루투스 연결 실패", Toast.LENGTH_SHORT).show()
        }
    }

    fun setPrintInfo() {
        var img = 0
        when(printType) {
            1 -> {
                printerModel = getString(R.string.skl_ts400b)
                img = R.drawable.skl_ts400b
            }
            2 -> {
                printerModel = getString(R.string.skl_te202)
                img = R.drawable.skl_te202
            }
            3 -> {
                printerModel = getString(R.string.sam4s)
                img = R.drawable.sam4s
            }
        }

        binding.ivPrinter.setImageResource(img)
        binding.btnPrinter.visibility = View.VISIBLE
        binding.btnPlus.visibility = View.INVISIBLE

        binding.printerModel.text = printerModel
        binding.tvNickPrinter.setTextColor(Color.BLACK)
        binding.nickPrinter.isEnabled = true
    }

    fun setConnStatus() {
        binding.ivStatus.setImageResource(R.drawable.icon_print_connection_on)
        binding.tvStatus.setTextColor(Color.BLACK)
        binding.tvStatus.text = getString(R.string.after_conn)
        binding.tvNickPrinter.setTextColor(Color.BLACK)
        binding.nickPrinter.isEnabled = true
        binding.btnRetry.visibility = View.GONE

//                setPrintConnStatus("Y")
    }

    fun setDisConnStatus() {
        binding.ivStatus.setImageResource(R.drawable.icon_print_connection_off)
        binding.tvStatus.setTextColor(Color.parseColor("#B4B4B4"))
        binding.tvStatus.text = getString(R.string.before_conn)
        binding.tvNickPrinter.setTextColor(Color.parseColor("#B4B4B4"))
        binding.nickPrinter.isEnabled = false
        binding.btnRetry.visibility = View.VISIBLE

//                setPrintConnStatus("N")
    }

    fun getPrintSetting() {
        ApiClient.service.getPrintContentSet(useridx, storeidx, androidId)
            .enqueue(object : Callback<PrintContentDTO> {
                override fun onResponse(call: Call<PrintContentDTO>, response: Response<PrintContentDTO>) {
                    Log.d(TAG, "프린터 출력 내용 조회 url : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return
                    when(result.status) {
                        1 -> {
                            if(result.admnick.isNotEmpty())
                                binding.nickDevice.text = result.admnick
                        }
                        else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }
            override fun onFailure(call: Call<PrintContentDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "프린터 출력 내용 조회 오류 >> $t")
                Log.d(TAG, "프린터 출력 내용 조회 오류 >> ${call.request()}")
            }
        })
    }

    fun showSetNickDialog(type: Int) {
        var nick = ""
        var model = ""
        var view = binding.nickDevice
        when(type) {
            1 -> {
                nick = ""
                model = "안드로이드 태블릿 PC"
            }
            2 -> {
                nick = printerNick
                model = printerModel
                view = binding.nickPrinter
            }
        }

        val nickDialog = SetNickDialog(nick, type, model)
        nickDialog.setOnNickChangeListener(object : DialogListener{
            override fun onNickSet(nick: String) {
                view.text = nick
            }
        })
        nickDialog.show(supportFragmentManager, "SetNickDialog")
    }

    fun setPrinterModel() {
        ApiClient.service.udtPrintModel(MyApplication.useridx, MyApplication.storeidx, MyApplication.androidId, printType, "Y")
            .enqueue(object : Callback<ResultDTO> {
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "신규 프린터 설정 url : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return
                    when(result.status) {
                        1 -> {
                            val intent = Intent(mActivity, SetConnActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                        }
                        else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "신규 프린터 설정 오류 >> $t")
                    Log.d(TAG, "신규 프린터 설정 오류 >> ${call.request()}")
                }
            })
    }

//    fun setPrintConnStatus(status: String) {
//        ApiClient.service.setPrintConnStatus(useridx, storeidx, androidId, bidx, status)
//            .enqueue(object : Callback<ResultDTO>{
//                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
//                    Log.d(TAG, "연결 프린터 상태 갱신 Url : $response")
//                    if(!response.isSuccessful) return
//
//                    val result = response.body() ?: return
//
//                    when(result.status) {
//                        1 -> {}
//                        else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
//                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
//                    Log.d(TAG, "연결 프린터 상태 갱신 오류 >> $t")
//                    Log.d(TAG, "연결 프린터 상태 갱신 오류 >> ${call.request()}")
//                }
//            })
//    }
}