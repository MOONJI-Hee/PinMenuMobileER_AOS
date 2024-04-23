package com.wooriyo.pinmenumobileer.qr

import android.app.DownloadManager
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.MyApplication.Companion.engStoreName
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.broadcast.DownloadReceiver
import com.wooriyo.pinmenumobileer.databinding.ActivityQrDetailBinding
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


class QrDetailActivity : BaseActivity() {
    lateinit var binding: ActivityQrDetailBinding
//    val mActivity = this@QrDetailActivity
//    val TAG = "QrDetailActivity"
    var seq = 1
    var strSeq = ""
    var qrCode : QrDTO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        seq = intent.getIntExtra("seq", seq)
        strSeq = AppHelper.intToString(seq)
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

        binding.run {
            back.setOnClickListener { finish() }
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
                if(qrCode != null) {
                    val uri = Uri.parse(qrCode!!.filePath.trim()) // 파일 주소 : 확장자명 포함되어야함

                    var fileName = "${strSeq}_${qrCode!!.tableNo}.png"
                    if(engStoreName.isNotEmpty()) {
                        fileName = "${engStoreName}_" + fileName
                    }

                    capture()

//                    val request = DownloadManager.Request(uri)
//                    val request = DownloadManager.Request(capture()?.toUri())

//                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) //진행 중, 완료 모두 노티 보여줌
//                    request.setTitle("핀메뉴 관리")
//                    request.setDescription("QR코드 다운로드 중") // [다운로드 중 표시되는 내용]
//                    request.setNotificationVisibility(1) // [앱 상단에 다운로드 상태 표시]
//                    request.setTitle(fileName) // [다운로드 제목 표시]
//                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName) // [다운로드 폴더 지정]
//
//                    val manager = mActivity.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
//                    val downloadID = manager.enqueue(request) // [다운로드 수행 및 결과 값 지정]
//
//                    val intentFilter = IntentFilter()
//                    intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
//                    registerReceiver(DownloadReceiver(mActivity), intentFilter)
                }
            }
        }
    }

    fun capture(): String? {
        var fileName = "${strSeq}_${qrCode!!.tableNo}.png"
        if(engStoreName.isNotEmpty()) {
            fileName = "${engStoreName}_" + fileName
        }

        val mPath = cacheDir.absolutePath+"/$fileName"

        var bitmap: Bitmap? = null
//        val captureView = window.decorView.rootView	//캡처할 뷰
        val captureView = binding.llQr

        bitmap = Bitmap.createBitmap(captureView.width, captureView.height, Bitmap.Config.ARGB_8888)

        var canvas = Canvas(bitmap)
        captureView.draw(canvas)

        if(bitmap == null) {
            return null
        }else {
            val imageFile = File(mPath)
            val outputStream = FileOutputStream(imageFile)
            outputStream.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
            }
        }
        return mPath
    }

    fun createQr() {
        ApiClient.imgService.createQr(MyApplication.useridx, MyApplication.storeidx, seq).enqueue(object : Callback<ResultDTO> {
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "Qr 생성 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when (result.status) {
                    1 -> {
                        qrCode = QrDTO(result.qidx, MyApplication.storeidx, seq, 1, result.filePath, "", getToday(), "N")

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
        ApiClient.imgService.udtQr(MyApplication.useridx, MyApplication.storeidx, qidx, tableNo).enqueue(object : Callback<ResultDTO> {
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