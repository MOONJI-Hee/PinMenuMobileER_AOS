package com.wooriyo.pinmenumobileer.printer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.MyApplication.Companion.osver
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.config.AppProperties.Companion.REQUEST_ENABLE_BT
import com.wooriyo.pinmenumobileer.config.AppProperties.Companion.REQUEST_LOCATION
import com.wooriyo.pinmenumobileer.databinding.ActivityPrinterMenuBinding
import com.wooriyo.pinmenumobileer.more.MoreActivity
import com.wooriyo.pinmenumobileer.store.StoreListActivity
import java.util.*

class PrinterMenuActivity : BaseActivity() {
    lateinit var binding: ActivityPrinterMenuBinding

    val mActivity = this@PrinterMenuActivity
    val TAG = "PrinterMenuActivity"

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
            binding.icPrinter.setOnClickListener {}
            binding.icMore.setOnClickListener { startActivity(Intent(mActivity, MoreActivity::class.java)) }

            connSet.setOnClickListener { startActivity(Intent(mActivity, SetConnActivity::class.java))}
            support.setOnClickListener { startActivity(Intent(mActivity, SupportPrinterActivity::class.java)) }
            contentSet.setOnClickListener { startActivity(Intent(mActivity, ContentSetActivity::class.java)) }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {
            REQUEST_ENABLE_BT -> {
                Log.d(TAG, "받아왓어")
                Log.d(TAG, "grantResults >>>$grantResults")

//                if(grantResults[0] == PackageManager.PERMISSION_DENIED) {
//                    getBluetoothPms()
//                }
            }
            REQUEST_LOCATION -> {
                Log.d(TAG, "위치권한 받아왔어")
                grantResults.forEach {
                    if(it == PackageManager.PERMISSION_DENIED) {
                        getLocationPms()
                        return
                    }
                }

                if (osver >= Build.VERSION_CODES.S) {
                    checkBluetoothPermission()
                    Log.d(TAG, "버전 높으니까 블루투스도 받아")

                }
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
            Log.d(TAG, "위치권한은 있음")
            if (osver >= Build.VERSION_CODES.S)
                checkBluetoothPermission()
        }else {
            Log.d(TAG, "위치권한은 없음")
            getLocationPms()
        }
    }

    fun checkBluetoothPermission() {
        Log.d(TAG, "버전 높으니까 블루투스도 받아2222")
        if(ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
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

    //권한 받아오기
    fun getLocationPms() {
        ActivityCompat.requestPermissions(mActivity, permissions, REQUEST_LOCATION)
        Log.d(TAG, "위치권한 받아")

    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun getBluetoothPms() {
        ActivityCompat.requestPermissions(mActivity, arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN), REQUEST_ENABLE_BT)
    }
}