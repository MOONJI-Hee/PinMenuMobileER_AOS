package com.wooriyo.pinmenumobileer.qr

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.config.AppProperties
import com.wooriyo.pinmenumobileer.databinding.ActivitySetEventBinding
import com.wooriyo.pinmenumobileer.model.EventDTO
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class SetEventActivity : BaseActivity() {
    lateinit var binding: ActivitySetEventBinding

    lateinit var event: EventDTO

    private var imgUri: Uri? = null
    var file: File? = null
    var delImg = 0

    val radius = (6*MyApplication.density).toInt()

    val mtpImg = MediaType.parse("image/*")
    val mtpTxt = MediaType.parse("text/plain")

    private val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    private val pickImg = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        if(it != null) {
            imgUri = it
            setImage(it)
        }
    }

    private val pickImgLgc = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK) {
            imgUri = it.data?.data
            if(imgUri != null) setImage(imgUri!!)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        event = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("event", EventDTO::class.java) as EventDTO
        }else
            intent.getSerializableExtra("event") as EventDTO


        if(!(event.img.isNullOrEmpty() || (event.img?:"").contains("noimage.png"))) {
            imgUri = (event.img)?.toUri()
            Glide.with(mActivity)
                .load(event.img)
                .transform(CenterCrop(), RoundedCorners(radius))
                .into(binding.img)
            binding.img.visibility = View.VISIBLE
            binding.delImg.visibility = View.VISIBLE
            binding.imgDefault.visibility = View.GONE
        }

        binding.exp.setText(event.content)
        binding.link.setText(event.link)

        binding.back.setOnClickListener { finish() }
        binding.thumb.setOnClickListener { checkPermissions() }

        binding.delImg.setOnClickListener {
            delImg = 1
            file = null
            imgUri = null
            binding.img.visibility = View.INVISIBLE
            binding.delImg.visibility = View.GONE
            binding.imgDefault.visibility = View.VISIBLE
        }

        binding.preview.setOnClickListener {
            if(check()) preview()
        }
        binding.save.setOnClickListener {
            if(check()) save()
        }
    }

    private fun check(): Boolean {
        event.content = binding.exp.text.toString()
        event.link = binding.link.text.toString()

        return if(event.content.isEmpty()) {
            Toast.makeText(mActivity, R.string.msg_no_event_exp, Toast.LENGTH_SHORT).show()
            false
        } else true
    }

    // 외부저장소 접근 권한 확인
    fun checkPermissions() {
        val deniedPms = ArrayList<String>()

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            for (pms in permission) {
                if(ActivityCompat.checkSelfPermission(mActivity, pms) != PackageManager.PERMISSION_GRANTED) {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(mActivity, pms)) {
                        AlertDialog.Builder(mActivity)
                            .setTitle(R.string.pms_storage_title)
                            .setMessage(R.string.pms_storage_content)
                            .setPositiveButton(R.string.confirm) { dialog, _ ->
                                dialog.dismiss()
                                getStoragePms()
                            }
                            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss()}
                            .show()
                        return
                    }else {
                        deniedPms.add(pms)
                    }
                }
            }
        }

        if(deniedPms.isNotEmpty()) {
            getStoragePms()
        }else {
            getImage()
        }
    }

    // 외부저장소 권한 받아오기
    fun getStoragePms() {
        ActivityCompat.requestPermissions(mActivity, permission, AppProperties.REQUEST_STORAGE)
    }

    fun getImage() {
        if(ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(mActivity)) {
            pickImg.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }else {
            pickImgLgc.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
        }
    }

    fun setImage(imgUri: Uri) {
        var path = ""

        contentResolver.query(imgUri, null, null, null, null)?.use { cursor ->
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            while (cursor.moveToNext()) {
                path = cursor.getString(dataColumn)
                Log.d(TAG, "path >>>>> $path")
            }
        }

        file = File(path)
        delImg = 0

        Glide.with(mActivity)
            .load(imgUri)
            .transform(CenterCrop(), RoundedCorners(radius))
            .into(binding.img)

        binding.img.visibility= View.VISIBLE
        binding.delImg.visibility = View.VISIBLE
        binding.imgDefault.visibility = View.GONE
    }

    private fun preview() {
        val intent = Intent(mActivity, EventPreviewActivity::class.java)
        intent.putExtra("exp", event.content)
        intent.putExtra("link", event.link)
        intent.putExtra("imgUri", imgUri)
        startActivity(intent)
    }

    private fun save() {
        var media: MultipartBody.Part? = null

        if(file != null) {
            val body = RequestBody.create(mtpImg, file!!)
            media = MultipartBody.Part.createFormData("img", file!!.name, body)
        }

        val expBody = RequestBody.create(mtpTxt, event.content)
        val linkBody = RequestBody.create(mtpTxt, event.link)

        ApiClient.service.setEventPopup(MyApplication.useridx, MyApplication.storeidx, expBody, linkBody, delImg, media)
            .enqueue(object : Callback<ResultDTO>{
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "주문완료 후 팝업 설정 url : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return
                    when(result.status) {
                        1 -> {
                            Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "주문완료 후 팝업 설정 실패 > $t")
                    Log.d(TAG, "주문완료 후 팝업 설정 실패 > ${call.request()}")
                }
        })
    }

}