package com.wooriyo.pinmenumobileer.menu

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenumobileer.MyApplication.Companion.useridx
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivityAddCategoryBinding
import com.wooriyo.pinmenumobileer.menu.dialog.DeleteDialog
import com.wooriyo.pinmenumobileer.model.CategoryDTO
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddCategoryActivity : BaseActivity() {
    lateinit var binding: ActivityAddCategoryBinding

    var type: Int = 1   // 1: 추가, 2: 수정
    var category: CategoryDTO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        type = intent.getIntExtra("type", type)

        if(type == 2) {
            binding.title.text = getText(R.string.title_cate_udt)
            binding.delete.visibility = View.VISIBLE

            category = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                intent.getSerializableExtra("category", CategoryDTO::class.java)
            else
                intent.getSerializableExtra("category") as CategoryDTO

            val cate = category
            if(cate != null) {
                binding.etName.setText(cate.name)
                binding.etSub.setText(cate.subname)
                binding.hide.isChecked = cate.buse == "N"
            }

            binding.save.setOnClickListener { modify() }
            binding.delete.setOnClickListener {
                DeleteDialog(1, category!!.name) { delete() }.show(supportFragmentManager, "DeleteDialog")
            }
        }else if(type == 1) {
            binding.save.setOnClickListener{ save() }
        }

        binding.back.setOnClickListener { finish() }
    }


    fun save() {
        val name = binding.etName.text.toString()
        val subName = binding.etSub.text.toString()

        val buse = if(binding.hide.isChecked) "N" else "Y"          // 버튼은 '숨기기' & buse는 사용여부이기 떄문에 체크가 되어있을 때 '사용안함' = N

        if(name.isEmpty()) {
            Toast.makeText(mActivity, R.string.msg_no_name, Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.service.insCate(useridx, storeidx, name, subName, buse)
            .enqueue(object: Callback<ResultDTO> {
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "카테고리 등록 url : $response")
                    if(!response.isSuccessful) return

                    val resultDTO = response.body() ?: return

                    when(resultDTO.status) {
                        1 -> {
                            Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        else -> Toast.makeText(mActivity, resultDTO.msg, Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "카테고리 등록 실패> ${call.request()}")
                    Log.d(TAG, "카테고리 등록 실패 > $t")
                }
            })
    }

    private fun modify() {
        val name = binding.etName.text.toString()
        val subName = binding.etSub.text.toString()

        val buse = if(binding.hide.isChecked) "N" else "Y"          // 버튼은 '숨기기' & buse는 사용여부이기 떄문에 체크가 되어있을 때 '사용안함' = N

        if(name.isEmpty()) {
            Toast.makeText(mActivity, R.string.msg_no_name, Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.service.udtCate(useridx, storeidx, category!!.idx, name, subName, buse)
            .enqueue(object: Callback<ResultDTO> {
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "카테고리 수정 url : $response")
                    if(!response.isSuccessful) {return}
                    val resultDTO = response.body()
                    if(resultDTO != null) {
                        when(resultDTO.status) {
                            1 -> {
                                Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                                val cate = category
                                if(cate != null) {
                                    cate.name = name
                                    cate.subname = subName
                                    cate.buse = buse

                                    Log.d(TAG, "category 수정 되었는지 확인 > ${category!!.name}")
                                    Log.d(TAG, "category 수정 되었는지 확인 > ${category!!.subname}")
                                    Log.d(TAG, "category 수정 되었는지 확인 > ${category!!.buse}")
                                }
                            }
                            else -> Toast.makeText(mActivity, resultDTO.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "카테고리 수정 실패> ${call.request()}")
                    Log.d(TAG, "카테고리 수정 실패 > $t")
                }
            })
    }

    fun delete() {
        ApiClient.service.delCate(useridx, storeidx, category!!.idx, category!!.code)
            .enqueue(object: Callback<ResultDTO> {
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "카테고리 삭제 url : $response")
                    if(!response.isSuccessful) return
                    val resultDTO = response.body()
                    if(resultDTO != null) {
                        when(resultDTO.status) {
                            1 -> {
                                Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            else -> Toast.makeText(mActivity, resultDTO.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "카테고리 삭제 실패 > ${call.request()}")
                    Log.d(TAG, "카테고리 삭제 실패 > $t")
                }
            })
    }
}