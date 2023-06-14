package com.wooriyo.pinmenumobileer.printer

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sewoo.port.android.BluetoothPort
import com.sewoo.request.android.RequestHandler
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication.Companion.useridx
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivitySetConnBinding
import com.wooriyo.pinmenumobileer.listener.DialogListener
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.model.PrintDTO
import com.wooriyo.pinmenumobileer.model.PrintListDTO
import com.wooriyo.pinmenumobileer.printer.adapter.PrinterAdapter
import com.wooriyo.pinmenumobileer.printer.dialog.SetNickDialog
import com.wooriyo.pinmenumobileer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class SetConnActivity : BaseActivity() {
    lateinit var binding: ActivitySetConnBinding

    val TAG = "SetConnActivity"
    val mActivity = this@SetConnActivity

    val printerList = ArrayList<PrintDTO>()
    val printerAdapter = PrinterAdapter(printerList)

    // 블루투스 관련
    lateinit var bluetoothManager: BluetoothManager
    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var remoteDevices: Vector<BluetoothDevice>

    //세우전자 Lib
    lateinit var bluetoothPort: BluetoothPort
    private val BT_PRINTER = 1536
    private var btThread: Thread? = null

    val arrRemoteDevice = java.util.ArrayList<String>()

    var searchflags = false

    // 브로드 리시버 시작
    val connectDevice =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (BluetoothDevice.ACTION_ACL_CONNECTED == action) {
                    Toast.makeText(getApplicationContext(), "BlueTooth Connect", Toast.LENGTH_SHORT).show();
                } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED == action) {
                    try {
                        if (bluetoothPort.isConnected()) bluetoothPort.disconnect()
                    } catch (e: IOException) {
                        // TODO Auto-generated catch block
                        e.printStackTrace()
                    } catch (e: InterruptedException) {
                        // TODO Auto-generated catch block
                        e.printStackTrace()
                    }
                    if (btThread != null) {
                        if(btThread!!.isAlive()) {
                            btThread!!.interrupt()
                            btThread = null
                        }
                    }
//                ConnectionFailedDevice()

                    Toast.makeText(getApplicationContext(), "BlueTooth Disconnect", Toast.LENGTH_SHORT).show();
                }
            }
        }

    val discoveryResult =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val key: String
                var bFlag = true
                var btDev: BluetoothDevice
                val remoteDevice =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                if (remoteDevice != null) {
                    val devNum = remoteDevice.bluetoothClass.majorDeviceClass
                    if (devNum != BT_PRINTER) return
                    key = if (remoteDevice.bondState != BluetoothDevice.BOND_BONDED) {
                        """${remoteDevice.name}[${remoteDevice.address}]""".trimIndent()
                    } else {
                        """${remoteDevice.name}[${remoteDevice.address}] [Paired]""".trimIndent()
                    }
                    if (bluetoothPort.isValidAddress(remoteDevice.address)) {
                        for (i in remoteDevices.indices) {
                            btDev = remoteDevices.elementAt(i)
                            if (remoteDevice.address == btDev.address) {
                                bFlag = false
                                break
                            }
                        }
                        if (bFlag) {
                            remoteDevices.add(remoteDevice)
                            arrRemoteDevice.add(key)
                        }
                        connDevice()
                    }
                }
            }
        }


    val searchStart =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                //Toast.makeText(mainView, "블루투스 기기 검색 시작", Toast.LENGTH_SHORT).show();
            }
        }


    val searchFinish =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                searchflags = true
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetConnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 블루투스 및 프린터 관련
        bluetoothManager = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        // BroadCast Receiver > 여기서 선언하는게 맞을지는 아직 모름
        registerReceiver(discoveryResult, IntentFilter(BluetoothDevice.ACTION_FOUND))
        registerReceiver(searchStart, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED))
        registerReceiver(searchFinish, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))

        remoteDevices = Vector<BluetoothDevice>()

        //세우전자 Lib
        bluetoothPort = BluetoothPort.getInstance()
        bluetoothPort.SetMacFilter(false) //not using mac address filtering

        printerAdapter.setConnClickListener(object : ItemClickListener{
            override fun onItemClick(position: Int) {
                val selType = printerList[position].printType

                //TODO selType으로 구분하여 프린트 연결
                searchDevice()
            }
        })

        binding.rvPrinter.layoutManager = LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false)
        binding.rvPrinter.adapter = printerAdapter

        val nickDialog = SetNickDialog("", 1, "안드로이드 스마트폰")
        nickDialog.setOnNickChangeListener(object : DialogListener{
            override fun onNickSet(nick: String) {
                binding.phoneNick.text = nick
            }
        })

        binding.back.setOnClickListener { finish() }
        binding.phoneNick.setOnClickListener { nickDialog.show(supportFragmentManager, "SetNickDialog") }
    }

    override fun onResume() {
        super.onResume()
        getConnPrintList()
    }

    fun getConnPrintList() {
        ApiClient.service.connPrintList(useridx).enqueue(object : Callback<PrintListDTO>{
            override fun onResponse(call: Call<PrintListDTO>, response: Response<PrintListDTO>) {
                Log.d(TAG, "등록된 프린터 리스트 조회 URL : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return

                when(result.status) {
                    1 -> {
                        printerList.clear()
                        printerList.addAll(result.myprintList)
                        printerAdapter.notifyDataSetChanged()
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PrintListDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "등록된 프린터 리스트 조회 오류 >> $t")
                Log.d(TAG, "등록된 프린터 리스트 조회 오류 >> ${call.request()}")
            }
        })
    }


    fun searchDevice() {
        Log.d(TAG, "블루투스 기기 검색 시작~!~!~!~!~!")
        bluetoothAdapter.startDiscovery()
    }

    fun connDevice() {
        Log.d(TAG, "블루투스 기기 커넥트")
        Log.d(TAG, "remote 기기 > $remoteDevices")
        if(remoteDevices.isNotEmpty()) {
            Log.d(TAG, "remote 기기 있음")

            val connDvc = remoteDevices[0]

            Log.d(TAG, "connDvc >> $connDvc")


            var retVal: Int = 0
            var str_temp = ""

            try {

                Log.d(TAG, "여기도 들어왔지렁")

                bluetoothPort.connect(connDvc)
                str_temp = connDvc.address
                retVal = Integer.valueOf(0)

                Log.d(TAG, "retVal >> $retVal")
            } catch (e: IOException) {
                e.printStackTrace()
                retVal = Integer.valueOf(-1)
            }

            if (retVal == 0) // Connection success.
            {
                Log.d(TAG, "retVal 0이면 성공이야....")

                val rh = RequestHandler()
                btThread = Thread(rh)
                btThread!!.start()

//                saveSettingFile()
                registerReceiver(connectDevice, IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED))
                registerReceiver(connectDevice, IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED))

            } else  // Connection failed.
            {
                Log.d(TAG, "retVal 0 아니면 실패야....")

            }
        }
    }
}