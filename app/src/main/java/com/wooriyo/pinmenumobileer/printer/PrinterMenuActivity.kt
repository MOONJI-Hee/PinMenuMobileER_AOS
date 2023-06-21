package com.wooriyo.pinmenumobileer.printer

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.MyApplication.Companion.androidId
import com.wooriyo.pinmenumobileer.MyApplication.Companion.osver
import com.wooriyo.pinmenumobileer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenumobileer.MyApplication.Companion.useridx
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.config.AppProperties
import com.wooriyo.pinmenumobileer.config.AppProperties.Companion.REQUEST_ENABLE_BT
import com.wooriyo.pinmenumobileer.config.AppProperties.Companion.REQUEST_LOCATION
import com.wooriyo.pinmenumobileer.databinding.ActivityPrinterMenuBinding
import com.wooriyo.pinmenumobileer.model.PrintDTO
import com.wooriyo.pinmenumobileer.model.PrintListDTO
import com.wooriyo.pinmenumobileer.more.MoreActivity
import com.wooriyo.pinmenumobileer.store.StoreListActivity
import com.wooriyo.pinmenumobileer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class PrinterMenuActivity : BaseActivity() {
    lateinit var binding: ActivityPrinterMenuBinding

    val TAG = "PrinterMenuActivity"
    val mActivity = this@PrinterMenuActivity

    val printerList = ArrayList<PrintDTO>()

    private val turnOnBluetoothResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == RESULT_OK) { }
    }

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrinterMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 권한 확인
        checkPermissions()

        binding.run {
            back.setOnClickListener { finish() }

            binding.icMain.setOnClickListener {
                MyApplication.setStoreDTO()
                MyApplication.storeidx = 0
                MyApplication.bidx = 0
                startActivity(Intent(mActivity, StoreListActivity::class.java))
            }
            binding.icPrinter.setOnClickListener {Toast.makeText(mActivity, R.string.msg_preparing, Toast.LENGTH_SHORT).show() }
            binding.icMore.setOnClickListener { startActivity(Intent(mActivity, MoreActivity::class.java)) }

            connSet.setOnClickListener {
                if(printerList.isEmpty()) {
                    startActivity(Intent(mActivity, NewConnActivity::class.java))
                }else {
                    val intent = Intent(mActivity, SetConnActivity::class.java)
                    intent.putExtra("printerList", printerList)
                    startActivity(intent)
                }
            }
            support.setOnClickListener { startActivity(Intent(mActivity, SupportPrinterActivity::class.java)) }
            contentSet.setOnClickListener { startActivity(Intent(mActivity, ContentSetActivity::class.java)) }
        }
    }

    override fun onResume() {
        super.onResume()
        getConnPrintList()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {
            REQUEST_ENABLE_BT -> {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkBluetooth()
                }
            }
            REQUEST_LOCATION -> {
                grantResults.forEach {
                    if(it == PackageManager.PERMISSION_DENIED) {
                        getLocationPms()
                        return
                    }
                }
                if (osver >= Build.VERSION_CODES.S)
                    checkBluetoothPermission()
                else
                    checkBluetooth()
            }
        }
    }

    // 권한 확인하기
    fun checkPermissions() {
        val deniedPms = ArrayList<String>()

        for (pms in permissions) {
            if(ActivityCompat.checkSelfPermission(mActivity, pms) != PackageManager.PERMISSION_GRANTED) {
                if(ActivityCompat.shouldShowRequestPermissionRationale(mActivity, pms)) {
                    AlertDialog.Builder(mActivity)
                        .setTitle(R.string.pms_location_content)
                        .setMessage(R.string.pms_location_content)
                        .setPositiveButton(R.string.confirm) { dialog, _ ->
                            dialog.dismiss()
                            deniedPms.add(pms)
                        }
                        .setNegativeButton(R.string.cancel) {dialog, _ -> dialog.dismiss()}
                        .show()
                }else {
                    deniedPms.add(pms)
                }
            }
        }

        if(deniedPms.isEmpty()) {
            if (osver >= Build.VERSION_CODES.S)
                checkBluetoothPermission()
            else
                checkBluetooth()
        }else {
            getLocationPms()
        }
    }

    fun checkBluetoothPermission() {
        if(ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            checkBluetooth()
        }else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.BLUETOOTH_CONNECT)) {
                AlertDialog.Builder(mActivity)
                    .setTitle(R.string.pms_bluetooth_title)
                    .setMessage(R.string.pms_bluetooth_content)
                    .setPositiveButton(R.string.confirm) { dialog, _ ->
                        dialog.dismiss()
                        getBluetoothPms()
                    }
                    .setNegativeButton(R.string.cancel) {dialog, _ -> dialog.dismiss()}
                    .show()
            }else getBluetoothPms()
        }
    }

    // 블루투스 ON/OFF 확인
    fun checkBluetooth() {
        if(!MyApplication.bluetoothAdapter.isEnabled) {   // 블루투스 꺼져있음
            turnOnBluetooth()
        }else {} // 켜져있음
    }

    //블루투스 ON
    private fun turnOnBluetooth() {
        turnOnBluetoothResult.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
    }

    //권한 받아오기
    fun getLocationPms() {
        ActivityCompat.requestPermissions(mActivity, permissions, REQUEST_LOCATION)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun getBluetoothPms() {
        ActivityCompat.requestPermissions(mActivity, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), REQUEST_ENABLE_BT)
    }

    fun getConnPrintList() {
        ApiClient.service.connPrintList(useridx, storeidx, androidId).enqueue(object :
            Callback<PrintListDTO> {
            override fun onResponse(call: Call<PrintListDTO>, response: Response<PrintListDTO>) {
                Log.d(TAG, "등록된 프린터 리스트 조회 URL : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return

                when(result.status) {
                    1 -> {
                        printerList.clear()
                        printerList.addAll(result.myprintList)
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
}