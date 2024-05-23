package com.wooriyo.pinmenumobileer.qr

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MainActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.MyApplication.Companion.engStoreName
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.common.dialog.AlertDialog
import com.wooriyo.pinmenumobileer.config.AppProperties
import com.wooriyo.pinmenumobileer.databinding.ActivityQrDetailBinding
import com.wooriyo.pinmenumobileer.databinding.QrInfoBinding
import com.wooriyo.pinmenumobileer.databinding.QrInfoReservBinding
import com.wooriyo.pinmenumobileer.model.QrDTO
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import com.wooriyo.pinmenumobileer.util.AppHelper
import com.wooriyo.pinmenumobileer.util.AppHelper.Companion.getToday
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class QrDetailActivity : BaseActivity() {
    lateinit var binding: ActivityQrDetailBinding

    var seq = 1
    var strSeq = ""
    var qrCode : QrDTO? = null
    var oriBuse: String = ""

    private val permission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        seq = intent.getIntExtra("seq", seq)
        strSeq = if(seq == 0) "예약" else AppHelper.intToString(seq)
        binding.tvSeq.text = strSeq

        qrCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("qrcode", QrDTO::class.java)
        } else {
            intent.getSerializableExtra("qrcode") as QrDTO?
        }

        if(qrCode == null) {
            binding.delete.isEnabled = false
            binding.save.isEnabled = false
            createQr()
        }else {
            binding.etTableNo.setText(qrCode?.tableNo)
            Glide.with(mActivity)
                .load(qrCode?.filePath)
                .into(binding.ivQr)
        }

        if(seq == 0) {
            binding.etTableNo.setText(R.string.reservation)
            binding.etTableNo.isEnabled = false

            binding.download.text = getString(R.string.qr_down_reserv)
            binding.copyLink.visibility = View.VISIBLE
            binding.delete.visibility = View.GONE
            binding.save.visibility = View.INVISIBLE
            binding.confirm.visibility = View.VISIBLE

            binding.qrInfoArea.layoutResource = R.layout.qr_info_reserv

            binding.qrInfoArea.setOnInflateListener { stub, inflated ->
                val bindingInfo = QrInfoReservBinding.bind(inflated)
                bindingInfo.pgStatus.text =
                    if(MyApplication.store.mid.isNullOrEmpty() || MyApplication.store.mid_key.isNullOrEmpty()) getString(R.string.qr_reserv_pg_unable) else getString(R.string.able)
            }
        }else {
            binding.qrInfoArea.layoutResource = R.layout.qr_info

            binding.qrInfoArea.setOnInflateListener { stub, inflated ->
                val bindingInfo = QrInfoBinding.bind(inflated)
                bindingInfo.postPay.isChecked = qrCode?.qrbuse == "Y"
                bindingInfo.postPay.setOnClickListener {
                    it as CheckBox
                    if(MyApplication.store.paytype == 2) {
                        qrCode?.qrbuse = if(it.isChecked) "Y" else "N"
                    }else {
                        it.isChecked = false
                        AlertDialog("", getString(R.string.dialog_no_business)).show(supportFragmentManager, "NoBusinessDialog")
                    }
                }
            }
        }

        binding.qrInfoArea.inflate()

        binding.run {
            back.setOnClickListener { finish() }
            confirm.setOnClickListener { finish() }
            save.setOnClickListener {
                val tableNo = binding.etTableNo.text.toString()

                if(tableNo.isEmpty())
                    Toast.makeText(mActivity, R.string.msg_no_table_no, Toast.LENGTH_SHORT).show()
                else {
                    if(qrCode != null) udtQr(qrCode!!.idx, tableNo)
                }
            }
            delete.setOnClickListener {
                if(qrCode != null) delQr(qrCode!!.idx)
            }
            download.setOnClickListener {
                if(qrCode == null) return@setOnClickListener

                if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                    checkPermissions()
                }else {
                    saveViewToImg(binding.qrArea)
                }
            }
            copyLink.setOnClickListener {
                val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                clipboardManager.setPrimaryClip(ClipData.newPlainText("url", qrCode?.url))

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
                    Toast.makeText(mActivity, R.string.msg_copy, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(grantResults.isEmpty()) return

        if(requestCode == AppProperties.REQUEST_STORAGE) {saveViewToImg(binding.qrArea)}
    }

    // 이미지 접근 권한 확인
    private fun checkPermissions() {
        val deniedPms = ArrayList<String>()

        for (pms in permission) {
//            when {
//                ActivityCompat.checkSelfPermission(mActivity, pms) != PackageManager.PERMISSION_GRANTED -> deniedPms.add(pms)
//
//                ActivityCompat.shouldShowRequestPermissionRationale(mActivity, pms) -> {
//                    AlertDialog.Builder(mActivity)
//                        .setTitle(R.string.pms_storage_title)
//                        .setMessage(R.string.pms_storage_content)
//                        .setPositiveButton(R.string.confirm) { dialog, _ ->
//                            dialog.dismiss()
//                            getStoragePms()
//                        }
//                        .setNegativeButton(R.string.cancel) {dialog, _ -> dialog.dismiss()}
//                        .show()
//                    return
//                }
//            }

            if(ActivityCompat.checkSelfPermission(mActivity, pms) != PackageManager.PERMISSION_GRANTED) {
                deniedPms.add(pms)
            }
        }

        if(deniedPms.isNotEmpty()) {
            getStoragePms()
        }else {
            saveViewToImg(binding.qrArea)
        }
    }

    //권한 받아오기
    fun getStoragePms() {
        ActivityCompat.requestPermissions(mActivity, permission, AppProperties.REQUEST_STORAGE)
    }

    fun saveViewToImg (view: View) {
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        view.draw(Canvas(bitmap))

        var fileName = "${strSeq}_${qrCode!!.tableNo}.jpg"
        if(engStoreName.isNotEmpty()) {
            fileName = "${engStoreName}_" + fileName
        }
        Log.d(TAG, "fileName > $fileName")

        val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
        val filePath = "$folder/$fileName"

        Log.d(TAG, "filePath > $filePath")

        val fos: FileOutputStream
        try{
            fos = FileOutputStream(File(filePath))
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos)
            fos.close()

            Toast.makeText(mActivity, R.string.msg_success_down, Toast.LENGTH_SHORT).show()
        }catch (e: IOException) {
            Toast.makeText(mActivity, R.string.msg_fail_down, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    fun createQr() {
        ApiClient.imgService.createQr(MyApplication.useridx, MyApplication.storeidx, seq).enqueue(object : Callback<ResultDTO> {
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "Qr 생성 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when (result.status) {
                    1 -> {
                        qrCode = QrDTO(result.qidx, MyApplication.storeidx, seq, 1, result.filePath, "", "", getToday(), "N", "Y")

                        binding.delete.isEnabled = true
                        binding.save.isEnabled = true

                        Glide.with(mActivity)
                            .load(result.filePath)
                            .into(binding.ivQr)
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Qr 생성 실패 >> $t")
                Log.d(TAG, "Qr 생성 실패 >> ${call.request()}")
            }
        })
    }

    fun udtQr(qidx: Int, tableNo: String) {
        ApiClient.imgService.udtQr(MyApplication.useridx, MyApplication.storeidx, qidx, tableNo, qrCode!!.qrbuse).enqueue(object : Callback<ResultDTO> {
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "Qr 등록 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when (result.status) {
                    1 -> Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Qr 등록 실패 >> $t")
                Log.d(TAG, "Qr 등록 실패 >> ${call.request()}")
            }
        })
    }

    fun delQr(qidx: Int) {
        ApiClient.imgService.delQr(MyApplication.useridx, MyApplication.storeidx, qidx).enqueue(object : Callback<ResultDTO> {
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "Qr 삭제 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when (result.status) {
                    1 ->  {
                        Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Qr 삭제 실패 >> $t")
                Log.d(TAG, "Qr 삭제 실패 >> ${call.request()}")
            }
        })
    }
}