package com.wooriyo.pinmenumobileer.history.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.wooriyo.pinmenumobileer.BaseDialogFragment
import com.wooriyo.pinmenumobileer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenumobileer.MyApplication.Companion.useridx
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.DialogTableNoBinding
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.history.adapter.TableNoAdapter
import com.wooriyo.pinmenumobileer.listener.DialogListener
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.model.TableNoDTO
import com.wooriyo.pinmenumobileer.model.TableNoListDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import retrofit2.Call
import retrofit2.Response


class SetTableNoDialog(val idx: Int, val dialogListener: DialogListener): BaseDialogFragment() {
    lateinit var binding: DialogTableNoBinding
    val TAG = context.toString()

    var tableNoList = ArrayList<TableNoDTO>()
    var tableNoAdapter = TableNoAdapter(tableNoList)

    var selpos = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogTableNoBinding.inflate(inflater)

        tableNoAdapter.setOnItemClickListener(object : ItemClickListener{
            override fun onItemClick(position: Int) {
                if(selpos == position) {
                    tableNoList[position].isChecked = false
                    selpos = -1
                } else {
                    if(selpos > -1) {
                        tableNoList[selpos].isChecked = false
                        tableNoAdapter.notifyItemChanged(selpos)
                    }
                    tableNoList[position].isChecked = true
                    selpos = position
                }
            }
        })

        binding.rvtableNo.layoutManager = GridLayoutManager(context, 3)
        binding.rvtableNo.adapter = tableNoAdapter

        binding.cancel.setOnClickListener { dismiss() }
        binding.save.setOnClickListener{
            if(selpos < 0) {
                Toast.makeText(context, R.string.msg_no_table_no, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            setTableNo()
        }

        getTableNo()

        return binding.root
    }

    fun getTableNo() {
        ApiClient.service.getTableNo(useridx, storeidx).enqueue(object : retrofit2.Callback<TableNoListDTO>{
            override fun onResponse(call: Call<TableNoListDTO>, response: Response<TableNoListDTO>) {
                Log.d(TAG, "테이블 번호 리스트 조회 url : $response")
                if(!response.isSuccessful) return

                val result = response.body()
                if(result != null){
                    when(result.status){
                        1 -> {
                            tableNoList.clear()
                            tableNoList.addAll(result.tableNoList)
                            tableNoAdapter.notifyDataSetChanged()
                        }
                        else -> Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<TableNoListDTO>, t: Throwable) {
                Toast.makeText(context, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "테이블 번호 리스트 조회 실패 >> $t")
                Log.d(TAG, "테이블 번호 리스트 조회 실패 >> ${call.request()}")
            }
        })
    }

    fun setTableNo() {
        val tableNo = tableNoList[selpos].no

        ApiClient.service.udtReservTableNo(useridx, storeidx, idx, tableNo).enqueue(object : retrofit2.Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "테이블 번호 변경 url : $response")
                if(!response.isSuccessful) return

                val result = response.body()
                if(result != null){
                    when(result.status){
                        1 -> {
                            Toast.makeText(context, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                            dialogListener.onTableNoSet(tableNo)
                            dismiss()
                        }
                        else -> Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(context, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "테이블 번호 변경 실패 >> $t")
                Log.d(TAG, "테이블 번호 변경 실패 >> ${call.request()}")
            }
        })
    }
}