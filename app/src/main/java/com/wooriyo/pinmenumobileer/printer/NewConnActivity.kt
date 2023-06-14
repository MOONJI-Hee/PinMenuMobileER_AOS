package com.wooriyo.pinmenumobileer.printer

import android.app.Activity
import android.app.Instrumentation
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.sewoo.port.android.BluetoothPort
import com.sewoo.request.android.RequestHandler
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivityNewConnBinding
import com.wooriyo.pinmenumobileer.listener.DialogListener
import com.wooriyo.pinmenumobileer.printer.dialog.SetNickDialog
import java.io.IOException
import java.util.*

class NewConnActivity : BaseActivity() {
    lateinit var binding: ActivityNewConnBinding

    var nickDialog : SetNickDialog ?= null

    val TAG = "NewConnActivity"
    val mActivity = this@NewConnActivity

    // 블루투스 관련
    lateinit var bluetoothManager: BluetoothManager
    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var remoteDevices: Vector<BluetoothDevice>

    //세우전자 Lib
    lateinit var bluetoothPort: BluetoothPort
    private val BT_PRINTER = 1536
    private var btThread: Thread? = null

    val arrRemoteDevice = ArrayList<String>()

    var searchflags = false

    var printType = 0
    var printNick = ""
    var printModel = ""

    val choosePrinterModel = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == RESULT_OK) {
            val data = it.data ?: return@registerForActivityResult

            printType = data.getIntExtra("printType", 0)

            setPrintInfo()
            searchDevice()
        }
    }

    private val turnOnBluetoothResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == RESULT_OK) {
//            getPairedDevice()
            searchDevice()
        }
    }


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
        binding = ActivityNewConnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener { finish() }
        binding.btnPlus.setOnClickListener {
            val intent = Intent(mActivity, SelectPrinterActivity::class.java)
            choosePrinterModel.launch(intent)
        }
        binding.nickDevice.setOnClickListener { setNickDialog("", 1, "안드로이드 스마트폰") }
        binding.nickPrinter.setOnClickListener { setNickDialog(printNick, 2, printModel) }

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
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (bluetoothPort.isConnected) {
                bluetoothPort.disconnect()
                unregisterReceiver(connectDevice)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        if (btThread != null && btThread!!.isAlive) {
            btThread!!.interrupt()
            btThread = null
        }

        unregisterReceiver(searchFinish)
        unregisterReceiver(searchStart)
        unregisterReceiver(discoveryResult)
    }

    fun getPairedDevice() {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
        pairedDevices?.forEach { device ->
//            val deviceName = device.name
            val deviceHardwareAddress = device.address // MAC address

            if(bluetoothPort.isValidAddress(deviceHardwareAddress)) {
                val deviceNum = device.bluetoothClass.majorDeviceClass

                if(deviceNum == BT_PRINTER) {
                    remoteDevices.add(device)
                }
            }
        }
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

                // 연결 되었으니까 뷰 바꾸기
                binding.ivStatus.setImageResource(R.drawable.icon_print_connection_on)
                binding.tvStatus.setTextColor(Color.BLACK)
                binding.tvStatus.text = getString(R.string.after_conn)
            } else  // Connection failed.
            {
                Log.d(TAG, "retVal 0 아니면 실패야....")

                binding.ivStatus.visibility = View.GONE
                binding.tvStatus.visibility = View.GONE
                binding.btnRetry.visibility = View.VISIBLE

//                AlertDialog.Builder(mActivity)
//                    .setTitle("Error")
//                    .setMessage("Failed to connect Bluetooth device.")
//                    .setNegativeButton(
//                        "CANCEL",
//                        DialogInterface.OnClickListener { dialog, which -> // TODO Auto-generated method stub
//                            dialog.dismiss()
//                        })
//                    .show()
            }
        }
    }

    fun setPrintInfo() {
        var img = 0
        when(printType) {
            1 -> {
                printModel = getString(R.string.skl_ts400b)
                img = R.drawable.skl_ts400b
            }
            2 -> {
                printModel = getString(R.string.skl_te202)
                img = R.drawable.skl_te202
            }
            3 -> {
                printModel = getString(R.string.sam4s)
                img = R.drawable.sam4s
            }
        }

        binding.ivPrinter.setImageResource(img)
        binding.btnPrinter.visibility = View.VISIBLE
        binding.btnPlus.visibility = View.INVISIBLE

        binding.printerModel.text = printModel
        binding.tvNickPrinter.setTextColor(Color.BLACK)
        binding.nickPrinter.isEnabled = true
    }

    fun setNickDialog(nick: String, type: Int, model: String) {
        if(nickDialog == null) {
            nickDialog = SetNickDialog(nick, type, model)

            nickDialog?.setOnNickChangeListener(object : DialogListener {
                override fun onNickSet(nick: String) {
                    when(type) {
                        1 -> binding.nickDevice.text = nick
                        2 -> binding.nickPrinter.text = nick
                    }
                }
            })
            nickDialog?.show(supportFragmentManager, "SetNickDialog")
        }
    }

    // 블루투스 ON/OFF 확인
    fun checkBluetooth() {
        if(!bluetoothAdapter.isEnabled) {   // 블루투스 꺼져있음
            turnOnBluetooth()
        }else {
            // TODO 블루투스 검색으로
//            getPairedDevice()
            searchDevice()
        }
    }

    //블루투스 ON
    private fun turnOnBluetooth() {
        turnOnBluetoothResult.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
    }
}