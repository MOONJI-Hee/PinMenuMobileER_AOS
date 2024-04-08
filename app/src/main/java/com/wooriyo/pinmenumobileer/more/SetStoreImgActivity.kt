package com.wooriyo.pinmenumobileer.more

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication.Companion.density
import com.wooriyo.pinmenumobileer.MyApplication.Companion.store
import com.wooriyo.pinmenumobileer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenumobileer.MyApplication.Companion.useridx
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.config.AppProperties.Companion.REQUEST_STORAGE
import com.wooriyo.pinmenumobileer.databinding.ActivitySetStoreImgBinding
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class SetStoreImgActivity : BaseActivity() {
    lateinit var binding: ActivitySetStoreImgBinding

    var imgUri: Uri ?= null
    var file: File? = null
    var delImg = 0

    private val pickImg = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        if(it != null) {
            imgUri = it
            setImage(it)
        }
        if(it != null) { setImage(it) }
    }

    private val pickImgLgc = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK) {
            imgUri = it.data?.data
            if(imgUri != null) setImage(imgUri!!)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetStoreImgBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(store.img.isNotEmpty()) {
            setImage(store.img.toUri())
        }
        if(!store.content.isNullOrEmpty()) {
            binding.etStoreExp.setText(store.content)
        }

        binding.back.setOnClickListener{finish()}
        binding.save.setOnClickListener{save()}
        binding.thumb.setOnClickListener{checkPms()}
        binding.delImg.setOnClickListener{delImage()}
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_STORAGE) {getImage()}
    }

    // 외부저장소 권한 확인
    fun checkPms() {
        when {
            ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> getImage()
            ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                AlertDialog.Builder(mActivity)
                    .setTitle(R.string.pms_storage_title)
                    .setMessage(R.string.pms_storage_content)
                    .setPositiveButton(R.string.confirm) { dialog, _ ->
                        dialog.dismiss()
                        getPms()
                    }
                    .setNegativeButton(R.string.cancel) {dialog, _ -> dialog.dismiss()}
                    .show()
            }
            else -> getPms()
        }
    }

    // 외부저장소 권한 받아오기
    fun getPms() {
        ActivityCompat.requestPermissions(mActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_STORAGE)
    }

    fun getImage() {
        if(ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(mActivity)) {
            pickImg.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }else {
            pickImgLgc.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
        }
    }

    private fun setImage(imgUri: Uri) {
        binding.img.visibility = View.VISIBLE
        binding.delImg.visibility = View.VISIBLE
        Glide.with(mActivity)
            .load(imgUri)
            .transform(CenterCrop(), RoundedCorners(6 * density.toInt()))
            .into(binding.img)
        binding.imgHint.visibility = View.GONE
        delImg = 0
    }

    fun delImage() {
        binding.img.visibility = View.INVISIBLE
        binding.delImg.visibility = View.INVISIBLE
        binding.imgHint.visibility = View.VISIBLE
        imgUri = null
        delImg = 1
    }

    private fun save() {
        var body: MultipartBody.Part? = null

        if(imgUri != null) {
            Log.d(TAG, "이미지 파일 경로 가져오기")


            var path = ""
            var name = ""

            contentResolver.query(imgUri!!, null, null, null, null)?.use { cursor ->
                // Cache column indices.
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    path = cursor.getString(dataColumn)
                    name = cursor.getString(nameColumn)
                    Log.d(TAG, "name >>>>> $name")
                    Log.d(TAG, "path >>>>> $path")
                }
            }

            val file = File(path)
            body = MultipartBody.Part.createFormData("img", file.name, RequestBody.create(MediaType.parse("image/*"), file))

            Log.d(TAG, "이미지 Uri >> $imgUri")
            Log.d(TAG, "이미지 절대경로 >> $path")
            Log.d(TAG, "이미지 File >> $file")
            Log.d(TAG, "이미지 body >> $body")
        }
        val exp = binding.etStoreExp.text.toString()
        val expBody = RequestBody.create(MediaType.parse("text/plain"), exp)

        ApiClient.service.udtStoreImg(useridx, storeidx, body, delImg, expBody)
            .enqueue(object: retrofit2.Callback<ResultDTO>{
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "매장 대표 사진 등록 url : $response")
                    if(response.isSuccessful) {
                        val resultDTO = response.body() ?: return
                        when(resultDTO.status) {
                            1 -> {
                                Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                                store.img = resultDTO.img
                                store.content = exp
                                setResult(RESULT_OK)
                                finish()
                            }
                            else -> Toast.makeText(mActivity, resultDTO.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "매장 대표 사진 등록 실패 > $t")
                }
            })
    }

}