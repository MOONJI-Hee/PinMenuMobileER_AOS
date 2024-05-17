package com.wooriyo.pinmenumobileer.printer

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sam4s.io.ethernet.SocketInfo
import com.sewoo.request.android.RequestHandler
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.MyApplication.Companion.androidId
import com.wooriyo.pinmenumobileer.MyApplication.Companion.bluetoothAdapter
import com.wooriyo.pinmenumobileer.MyApplication.Companion.remoteDevices
import com.wooriyo.pinmenumobileer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenumobileer.MyApplication.Companion.useridx
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.broadcast.BtDiscoveryReceiver
import com.wooriyo.pinmenumobileer.config.AppProperties
import com.wooriyo.pinmenumobileer.databinding.ActivitySetConnBinding
import com.wooriyo.pinmenumobileer.listener.DialogListener
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.model.PrintContentDTO
import com.wooriyo.pinmenumobileer.printer.adapter.Sam4sAdapter
import com.wooriyo.pinmenumobileer.printer.adapter.SewooAdapter
import com.wooriyo.pinmenumobileer.printer.dialog.SetNickDialog
import com.wooriyo.pinmenumobileer.util.ApiClient
import com.wooriyo.pinmenumobileer.util.AppHelper
import com.wooriyo.pinmenumobileer.util.AppHelper.Companion.connDevice
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class SetConnActivity : BaseActivity() {
    lateinit var binding: ActivitySetConnBinding

    lateinit var cubeList : ArrayList<SocketInfo>
    lateinit var sam4sAdapter: Sam4sAdapter

//    val printerList = ArrayList<PrintDTO>()
//    val printerAdapter = PrinterAdapter(printerList)

    val sewooAdapter = SewooAdapter(remoteDevices)

    var connPos = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetConnBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        cubeList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            (intent.getParcelableArrayListExtra("cubeList", SocketInfo::class.java) ?: ArrayList<SocketInfo>())
//        }else {
//            (intent.getParcelableArrayListExtra("cubeList") ?: ArrayList<SocketInfo>())
//        }

        this.cubeList = AppHelper.cubePrinterList

        Log.d(TAG, "cubeList 들어왔니??? >> $cubeList")

        sam4sAdapter = Sam4sAdapter(cubeList)

        // 디바이스, 프린터 정보 조회
        getPrintSetting()

        // 페어링된 프린터 리스트 조회
//        getPairedDevice()

        // BroadCast Receiver
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent == null) return

                var bFlag = true
                var btDev: BluetoothDevice
                val remoteDevice = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                if (remoteDevice != null) {
                    val devNum = remoteDevice.bluetoothClass.majorDeviceClass
                    if (devNum != AppProperties.BT_PRINTER) return

                    if (MyApplication.bluetoothPort.isValidAddress(remoteDevice.address)) {
                        for (i in remoteDevices.indices) {
                            btDev = remoteDevices.elementAt(i)
                            if (remoteDevice.address == btDev.address) {
                                bFlag = false
                                break
                            }
                        }
                        if (bFlag) {
                            remoteDevices.add(remoteDevice)
                        }
                    }

                    sewooAdapter.notifyDataSetChanged()
                    val retVal = AppHelper.connDevice(0)

                    if (retVal == 0) { // Connection success.
                        val rh = RequestHandler()
                        MyApplication.btThread = Thread(rh)
                        MyApplication.btThread!!.start()
                    } else // Connection failed.
                        Toast.makeText(context, "블루투스 연결 실패", Toast.LENGTH_SHORT).show()
                }else
                    Toast.makeText(context, "검색된 블루투스 기기가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }, IntentFilter(BluetoothDevice.ACTION_FOUND))
//        registerReceiver(connectDevice, IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED))
//        registerReceiver(connectDevice, IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED))

        // 연결 프린트 리사이클러뷰
        sewooAdapter.setConnClickListener(object : ItemClickListener {
            override fun onItemClick(position: Int) {
                var status = ""

                if (remoteDevices[position].bondState == 0) {
                    MyApplication.bluetoothPort.disconnect()

//                    printerList[position].connected = false

                    status = "N"
                } else {
                    val retVal = connDevice(position)

                    if (retVal == 0) {
                        val rh = RequestHandler()
                        MyApplication.btThread = Thread(rh)
                        MyApplication.btThread!!.start()

//                        printerList[position].connected = true

                        status = "Y"

                        val prePos = connPos
                        connPos = position
                        sewooAdapter.notifyItemChanged(position)

//                        if (prePos != connPos)
//                            printerAdapter.notifyItemChanged(prePos)

                    }else if (retVal == -2) {
                        AppHelper.searchDevice()
                    } else {
                        Toast.makeText(mActivity, "블루투스 연결 실패", Toast.LENGTH_SHORT).show()
                        status = "N"
                    }
                }
                val prePos = connPos
                connPos = position
                sewooAdapter.notifyItemChanged(position)

                if (prePos != connPos)
                    sewooAdapter.notifyItemChanged(prePos)

//                setPrintConnStatus(position, status)
            }
        })

        sam4sAdapter.setConnClickListener(object: ItemClickListener {
            override fun onItemClick(position: Int) {
                AppHelper.connectCube(mActivity, cubeList[position])
            }
        })

//        binding.rvPrinter.layoutManager = LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false)
//        binding.rvPrinter.adapter = printerAdapter

        binding.rvSewoo.layoutManager = LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false)
        binding.rvSewoo.adapter = sewooAdapter

        binding.rvSam4s.layoutManager = LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false)
        binding.rvSam4s.adapter = sam4sAdapter

        val nickDialog = SetNickDialog("", 1, "안드로이드 스마트폰")
        nickDialog.setOnNickChangeListener(object : DialogListener{
            override fun onNickSet(nick: String) {
                binding.phoneNick.text = nick
            }
        })

        binding.back.setOnClickListener { finish() }
        binding.phoneNick.setOnClickListener { nickDialog.show(supportFragmentManager, "SetNickDialog") }
        binding.plus.setOnClickListener {
            bluetoothAdapter.startDiscovery()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    fun getPrintSetting() {
        ApiClient.service.getPrintContentSet(useridx, storeidx, androidId).enqueue(object : Callback<PrintContentDTO>{
            override fun onResponse(call: Call<PrintContentDTO>, response: Response<PrintContentDTO>) {
                Log.d(TAG, "프린터 출력 내용 조회 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status) {
                    1 -> {
                        if(result.admnick.isNotEmpty())
                            binding.phoneNick.text = result.admnick
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

    fun setPrintConnStatus(position: Int, status: String) {
//        ApiClient.service.setPrintConnStatus(useridx, storeidx, androidId, printerList[position].idx, status)
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
    }

//    fun getConnPrintList() {
//        ApiClient.service.connPrintList(useridx, storeidx, androidId).enqueue(object : Callback<PrintListDTO> {
//            override fun onResponse(call: Call<PrintListDTO>, response: Response<PrintListDTO>) {
//                Log.d(TAG, "등록된 프린터 리스트 조회 URL : $response")
//                if(!response.isSuccessful) return
//
//                val result = response.body() ?: return
//
//                when(result.status) {
//                    1 -> {
//                        printerList.clear()
//                        printerList.addAll(result.myprintList)
//                        printerAdapter.notifyDataSetChanged()
//
//                        checkConn()
//                    }
//                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<PrintListDTO>, t: Throwable) {
//                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
//                Log.d(TAG, "등록된 프린터 리스트 조회 오류 >> $t")
//                Log.d(TAG, "등록된 프린터 리스트 조회 오류 >> ${call.request()}")
//            }
//        })
//    }
}