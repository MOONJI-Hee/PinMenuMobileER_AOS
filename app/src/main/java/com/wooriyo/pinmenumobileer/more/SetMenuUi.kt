package com.wooriyo.pinmenumobileer.more

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenumobileer.MyApplication.Companion.useridx
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivitySetMenuUiBinding
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import retrofit2.Call
import retrofit2.Response

class SetMenuUi : BaseActivity() {
    lateinit var binding: ActivitySetMenuUiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetMenuUiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when(MyApplication.store.viewmode) {
            "b" -> binding.checkBasic.isChecked = true
            "p" -> binding.check3x3.isChecked = true
            "l" -> binding.checkList.isChecked = true
        }

        binding.run{
            checkBasic.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    check3x3.isChecked = false
                    checkList.isChecked = false
                    save()
                }
            }
            check3x3.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked)  {
                    checkBasic.isChecked = false
                    checkList.isChecked = false
                    save()
                }
            }
            checkList.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    checkBasic.isChecked = false
                    check3x3.isChecked = false
                    save()
                }
            }

            back.setOnClickListener { finish() }
        }
    }

    fun save() {
        val selColor = MyApplication.store.bgcolor
        var selMode = ""

        when {
            binding.checkBasic.isChecked -> selMode = "b"
            binding.check3x3.isChecked -> selMode = "p"
            binding.checkList.isChecked -> selMode = "l"
        }

        ApiClient.service.setThema(useridx, storeidx, selColor, selMode)
            .enqueue(object: retrofit2.Callback<ResultDTO> {
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "메뉴 테마 설정 url > $response")
                    if (!response.isSuccessful) return

                    val result = response.body() ?: return
                    when(result.status) {
                        1 -> {
                            MyApplication.store.bgcolor = selColor
                            MyApplication.store.viewmode = selMode
                            Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                        }
                        else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "메뉴 테마 설정 실패 > $t")
                    Log.d(TAG, "메뉴 테마 설정 실패 > ${call.request()}")
                }
            })

    }
}