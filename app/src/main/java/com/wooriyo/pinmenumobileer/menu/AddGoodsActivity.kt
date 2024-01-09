package com.wooriyo.pinmenumobileer.menu

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenumobileer.MyApplication.Companion.useridx
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivityAddGoodsBinding
import com.wooriyo.pinmenumobileer.menu.dialog.DeleteDialog
import com.wooriyo.pinmenumobileer.model.GoodsDTO
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import com.wooriyo.pinmenumobileer.util.AppHelper
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddGoodsActivity : BaseActivity() {
    lateinit var binding: ActivityAddGoodsBinding

    var cate: String = ""
    var type: Int = 1   // 1: 추가, 2: 수정
    var goods: GoodsDTO ?=  null

    val pickImg = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        if(it != null) {
            val radius = (6 * MyApplication.density).toInt()

            Glide.with(mActivity)
                .load(it)
                .transform(CenterCrop(), RoundedCorners(radius))
                .into(binding.img1)
            binding.imgHint1.visibility = View.GONE
            binding.img1.visibility = View.VISIBLE

            Log.d(TAG, "이미지 Uri >> $it")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGoodsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.price.addTextChangedListener(object: TextWatcher{
            var result = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!s.isNullOrEmpty() && s.toString() != result) {
                    result = AppHelper.price(s.toString().replace(",", "").toInt())
                    binding.price.setText(result)
                    binding.price.setSelection(result.length)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.status.adapter = ArrayAdapter(mActivity, R.layout.spinner_menu_status, R.id.item, resources.getStringArray(R.array.menu_icon))

        binding.back.setOnClickListener { finish() }
        binding.save.setOnClickListener { getMenu() }

        type = intent.getIntExtra("type", type)

        if(type == 2) {
            binding.title.text = getText(R.string.menu_udt)
            binding.delete.visibility = View.VISIBLE

            goods = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                intent.getSerializableExtra("goods", GoodsDTO::class.java)
            else
                intent.getSerializableExtra("goods") as GoodsDTO

            setMenu(goods)

            binding.delete.setOnClickListener {
                DeleteDialog(2, goods!!.name) { delete() }.show(supportFragmentManager, "DeleteDialog")
            }
        }else {
            cate = intent.getStringExtra("cate") ?: ""
        }


        // 사진 선택 도구
        binding.thum1.setOnClickListener {
            pickImg.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    fun setMenu(gd: GoodsDTO?) {
        if(gd != null) {
            binding.run {
                name.setText(gd.name)
                explain.setText(gd.content)

                cookingTimeMin.setText(gd.cooking_time_min)
                cookingTimeMax.setText(gd.cooking_time_max)

                price.setText(AppHelper.price(gd.price))

                val radius = (6 * MyApplication.density).toInt()

                if(!gd.img1.isNullOrEmpty()) {
                    Glide.with(mActivity)
                        .load(gd.img1)
                        .transform(CenterCrop(), RoundedCorners(radius))
                        .into(img1)
                    imgHint1.visibility = View.GONE
                    binding.img1.visibility = View.VISIBLE
                }

                if(!gd.img2.isNullOrEmpty()) {
                    Glide.with(mActivity)
                        .load(gd.img2)
                        .transform(CenterCrop(), RoundedCorners(radius))
                        .into(img2)
                    imgHint2.visibility = View.GONE
                    binding.img2.visibility = View.VISIBLE
                }

                if(!gd.img3.isNullOrEmpty()) {
                    Glide.with(mActivity)
                        .load(gd.img3)
                        .transform(CenterCrop(), RoundedCorners(radius))
                        .into(img3)
                    imgHint3.visibility = View.GONE
                    binding.img3.visibility = View.VISIBLE
                }

                useSleep.isChecked = gd.adDisplay == "Y"
                useOpt.isChecked = gd.boption == "Y"

                status.setSelection(gd.icon - 1)
                Log.d(TAG, "gd.icon -1 >> ${gd.icon - 1}")
                Log.d(TAG, "Selected Item >> ${status.selectedItemPosition}")
            }
        }
    }

    fun getMenu() {
        val gd = goods ?: GoodsDTO(cate)

        binding.run {
            val strName = name.text.toString()

            if(strName.isEmpty()){
                Toast.makeText(mActivity, R.string.msg_no_goods_name, Toast.LENGTH_SHORT).show()
                return
            }

            var strCookTimeMin = cookingTimeMin.text.toString()
            var strCookTimeMax = cookingTimeMax.text.toString()
            var strPrice = price.text.toString().replace(",", "")

            if(strCookTimeMin.isEmpty())
                strCookTimeMin = "0"

            if(strCookTimeMax.isEmpty())
                strCookTimeMax = "0"

            if(strPrice.isEmpty())
                strPrice = "0"

            gd.name = name.text.toString()
            gd.content = explain.text.toString()
            gd.cooking_time_min = strCookTimeMin
            gd.cooking_time_max = strCookTimeMax
            gd.price = strPrice.toInt()

            if(useSleep.isChecked) gd.adDisplay = "Y" else gd.adDisplay = "N"

            gd.icon = status.selectedItemPosition + 1
        }

        when(type) {
            1 -> save(gd)
            2 -> modify(gd)
        }
    }

    fun save(gd: GoodsDTO) {
        ApiClient.service.insGoods(useridx, storeidx, gd.category, gd.name, gd.content?:"", gd.cooking_time_min, gd.cooking_time_max, gd.price,
        gd.adDisplay, gd.icon, gd.boption, "", "", "", "", "").enqueue(object: Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "메뉴 등록 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return

                if(result.status == 1) {
                    Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
//                    uploadImage(gd.idx, media1, media2, media3)
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "메뉴 등록 실패> ${call.request()}")
                Log.d(TAG, "메뉴 등록 실패 > $t")
            }
        })
    }

    fun modify(gd: GoodsDTO) {
        ApiClient.service.udtGoods(useridx, storeidx, gd.idx, gd.category, gd.name, gd.content?:"", gd.cooking_time_min, gd.cooking_time_max, gd.price,
        gd.adDisplay, gd.icon, gd.boption, "", "", "", "", "", "").enqueue(object: Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "메뉴 수정 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return

                if(result.status == 1) {
                    Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
//                    uploadImage(gd.idx, media1, media2, media3)
                    // TODO 등록된 사진 중 삭제할 것 태우기
                }else {
                    Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "메뉴 수정 실패> ${call.request()}")
                Log.d(TAG, "메뉴 수정 실패 > $t")
            }
        })
    }

    fun delete() {
        ApiClient.service.delGoods(useridx, storeidx, goods!!.idx).enqueue(object: Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "메뉴 삭제 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return

                if(result.status == 1) {
                    Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                    finish()
                }else {
                    Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "메뉴 삭제 실패> ${call.request()}")
                Log.d(TAG, "메뉴 삭제 실패 > $t")
            }
        })

    }

    fun uploadImage(gidx: Int, media1: MultipartBody.Part?, media2: MultipartBody.Part?, media3: MultipartBody.Part?) {
        ApiClient.imgService.uploadImg(useridx, gidx, media1, media2, media3)
            .enqueue(object : Callback<ResultDTO>{
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "이미지 등록 url : $response")
                    if(!response.isSuccessful) return

                    val result = response.body()
                    if(result != null) {
                        when(result.status){
                            1 -> {
                                Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                            }
                            else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "이미지 등록 실패 > $t")
                    Log.d(TAG, "이미지 등록 실패 > ${call.request()}")
                }
            })
    }
}