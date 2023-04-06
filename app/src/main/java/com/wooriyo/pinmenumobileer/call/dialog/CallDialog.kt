package com.wooriyo.pinmenumobileer.call.dialog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.wooriyo.pinmenumobileer.BaseDialog
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.DialogCallBinding
import com.wooriyo.pinmenumobileer.model.CallDTO
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CallDialog(context: Context, val type: Int, val call: CallDTO?): BaseDialog(context) {
    lateinit var binding: DialogCallBinding

    val TAG = "CallDialog"

    var name = ""
    var idx = 0             // 추가 : storeidx, 수정 : callidx

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idx = MyApplication.storeidx

        if(type == 1 && call != null) { //수정
            binding.title.text = context.getString(R.string.call_udt)
            binding.etName.setText(call.name)
            binding.save.visibility = View.GONE
            binding.llUdt.visibility = View.VISIBLE
            idx = call.idx
        }

        binding.close.setOnClickListener{dismiss()}
        binding.save.setOnClickListener{save()}
        binding.modify.setOnClickListener{modify()}
        binding.delete.setOnClickListener{delete()}
    }

    fun check(): Boolean {
        name = binding.etName.text.toString()

        return if(name.isEmpty()) {
            Toast.makeText(context, R.string.msg_no_item_name, Toast.LENGTH_SHORT).show()
            false
        }else { true }
    }

    fun save() {
        if(!check()) return

        ApiClient.service.insCall(useridx, idx, name).enqueue(object : Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "호출 목록 등록 url : $response")
                if(!response.isSuccessful) return

                val result = response.body()
                if(result != null) {
                    when(result.status) {
                        1 -> {
                            Toast.makeText(context, R.string.complete, Toast.LENGTH_SHORT).show()
                            dismiss()
                        }
                        else -> Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }

            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(context, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "호출 목록 등록 실패 > $t")
                Log.d(TAG, "호출 목록 등록 실패 > ${call.request()}")
            }
        })
    }

    fun modify() {
        if(!check()) return

        ApiClient.service.udtCall(useridx, idx, name).enqueue(object : Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "호출 목록 수정 url : $response")
                if(!response.isSuccessful) return

                val result = response.body()
                if(result != null) {
                    when(result.status) {
                        1 -> {
                            Toast.makeText(context, R.string.complete, Toast.LENGTH_SHORT).show()
                            dismiss()
                        }
                        else -> Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }

            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(context, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "호출 목록 수정 실패 > $t")
                Log.d(TAG, "호출 목록 수정 실패 > ${call.request()}")
            }
        })
    }

    fun delete() {
        ApiClient.service.delCall(useridx, idx).enqueue(object : Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "호출 목록 삭제 url : $response")
                if(!response.isSuccessful) return

                val result = response.body()
                if(result != null) {
                    when(result.status) {
                        1 -> {
                            Toast.makeText(context, R.string.complete, Toast.LENGTH_SHORT).show()
                            dismiss()
                        }
                        else -> Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(context, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "호출 목록 삭제 실패 > $t")
                Log.d(TAG, "호출 목록 삭제 실패 > ${call.request()}")
            }
        })
    }
}