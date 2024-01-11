package com.wooriyo.pinmenumobileer.qr

import android.app.DownloadManager
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.wooriyo.pinmenumobileer.MainActivity
import com.wooriyo.pinmenumobileer.MyApplication.Companion.engStoreName
import com.wooriyo.pinmenumobileer.MyApplication.Companion.store
import com.wooriyo.pinmenumobileer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenumobileer.MyApplication.Companion.useridx
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.broadcast.DownloadReceiver
import com.wooriyo.pinmenumobileer.databinding.FragmentSetQrcodeBinding
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.model.QrDTO
import com.wooriyo.pinmenumobileer.model.QrListDTO
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.qr.adapter.QrAdapter
import com.wooriyo.pinmenumobileer.qr.dialog.QrInfoDialog
import com.wooriyo.pinmenumobileer.util.ApiClient
import com.wooriyo.pinmenumobileer.util.AppHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SetQrcodeFragment : Fragment() {
    lateinit var binding: FragmentSetQrcodeBinding
    val TAG = "SetQrcodeFragment"

    val qrList = ArrayList<QrDTO>()
    val qrAdapter = QrAdapter(qrList)

    var qrCnt = 0
    var storeName = ""

    var bisAll = false
    var bisCnt = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSetQrcodeBinding.inflate(layoutInflater)

        bisAll = store.qrbuse == "Y"
        binding.postPayAll.isChecked = bisAll

        qrAdapter.setOnPostPayClickListener(object: ItemClickListener{
            override fun onQrClick(position: Int, status: Boolean) {
                if(status) bisCnt++ else bisCnt--

                Log.d(TAG, "checked Toggle Cnt > $bisCnt")

                if(bisCnt == qrList.size) {
                    binding.postPayAll.isChecked = true
                    setAllPostPay("Y")
                }else {
                    if(binding.postPayAll.isChecked) {
                        binding.postPayAll.isChecked = false
                    }
                    val buse = if(status) "Y" else "N"
                    setPostPay(qrList[position].idx, buse, position)
                }
            }
        })

        binding.rvQr.run {
            layoutManager = GridLayoutManager(context, 2)
            adapter = qrAdapter
        }

        binding.run {
            saveName.setOnClickListener { udtStoreName() }
            downAll.setOnClickListener { downloadAll() }
            postPayAll.setOnClickListener {
                it as CheckBox
                val buse = if(it.isChecked) "Y" else "N"
                setAllCheck(buse)
                setAllPostPay(buse)
            }
            btnInfo.setOnClickListener {
                QrInfoDialog().show((activity as MainActivity).supportFragmentManager, "QrInfoDialog")
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getQrList()
    }

    fun downloadAll() {
        val manager = context?.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager

        qrList.forEach {
            val uri = Uri.parse(it.filePath.trim())
            var fileName = "${AppHelper.intToString(it.seq)}_${it.tableNo}.png"
            if(engStoreName.isNotEmpty()) {
                fileName = "${engStoreName}_" + fileName
            }

            val request = DownloadManager.Request(uri)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) //진행 중, 완료 모두 노티 보여줌
            request.setTitle("핀메뉴 관리")
            request.setDescription("QR코드 다운로드 중") // [다운로드 중 표시되는 내용]
            request.setNotificationVisibility(1) // [앱 상단에 다운로드 상태 표시]
            request.setTitle(fileName) // [다운로드 제목 표시]
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName) // [다운로드 폴더 지정]

            val downloadID = manager.enqueue(request) // [다운로드 수행 및 결과 값 지정]

            val intentFilter = IntentFilter()
            intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            (activity as MainActivity).registerReceiver(DownloadReceiver(requireContext()), intentFilter)
        }
    }

    fun getQrList() {
        ApiClient.imgService.getQrList(useridx, storeidx).enqueue(object : Callback<QrListDTO>{
            override fun onResponse(call: Call<QrListDTO>, response: Response<QrListDTO>) {
                Log.d(TAG, "QR 리스트 조회 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when (result.status) {
                    1 -> {
                        qrList.clear()
                        qrList.addAll(result.qrList)

                        qrCnt = result.qrCnt
                        binding.qrCnt.text = (qrCnt - qrList.size).toString()

                        storeName = result.enname
                        if(storeName != null && storeName != "") {
                            binding.etStoreName.setText(storeName)
                            engStoreName = storeName
                        }
                        qrAdapter.setQrCount(qrCnt)
                        qrAdapter.notifyDataSetChanged()

                        bisCnt = 0
                        qrList.forEach {
                            if(it.qrbuse == "Y") bisCnt++
                        }
                    }

                    else -> Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<QrListDTO>, t: Throwable) {
                Toast.makeText(context, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "QR 리스트 조회 실패 >> $t")
                Log.d(TAG, "QR 리스트 조회 실패 >> ${call.request()}")
            }
        })
    }

    fun udtStoreName() {
        storeName = binding.etStoreName.text.toString()

        if(storeName.isEmpty()) {
            Toast.makeText(context, R.string.store_name_hint, Toast.LENGTH_SHORT).show()
        }else {
            ApiClient.imgService.udtStoreName(useridx, storeidx, storeName).enqueue(object : Callback<ResultDTO>{
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "영문 매장 이름 등록 url : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return
                    when (result.status) {
                        1 -> {
                            Toast.makeText(context, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                            engStoreName = storeName
                        }
                        else -> Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(context, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "영문 매장 이름 등록 실패 >> $t")
                    Log.d(TAG, "영문 매장 이름 등록 실패 >> ${call.request()}")
                }
            })
        }
    }

    fun setPostPay(qidx: Int, buse: String, position: Int) {
        ApiClient.service.setPostPay(useridx, storeidx, qidx, buse).enqueue(object : Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "QR 후불 결제 설정 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when (result.status) {
                    1 -> {
                        qrList[position].qrbuse = buse
                        qrAdapter.notifyItemChanged(position)
                    }
                    else -> Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(context, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "QR 후불 결제 설정 실패 >> $t")
                Log.d(TAG, "QR 후불 결제 설정 실패 >> ${call.request()}")
            }
        })
    }

    fun setAllPostPay(buse: String) {
        ApiClient.service.setPostPayAll(useridx, storeidx, buse).enqueue(object : Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "QR 후불 결제 전체 설정 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when (result.status) {
                    1 -> {
                        store.qrbuse = buse
                    }
                    else -> Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(context, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "QR 후불 결제 전체 설정 실패 >> $t")
                Log.d(TAG, "QR 후불 결제 전체 설정 실패 >> ${call.request()}")
            }
        })
    }

    fun setAllCheck(buse: String) {
        bisCnt = if(buse == "Y") qrList.size else 0

        for(i in 0 until qrList.size) {
            if(qrList[i].qrbuse != buse){
                qrList[i].qrbuse = buse
                qrAdapter.notifyItemChanged(i)
            }
        }
//        qrList.forEach { it.qrbuse = buse }
//        qrAdapter.notifyItemRangeChanged(0, qrList.size)
    }

    companion object {
        @JvmStatic
        fun newInstance() = SetQrcodeFragment()
    }
}