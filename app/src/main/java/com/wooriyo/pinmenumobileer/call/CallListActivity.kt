package com.wooriyo.pinmenumobileer.call

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenumobileer.MyApplication.Companion.useridx
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.call.adapter.CallListAdapter
import com.wooriyo.pinmenumobileer.common.dialog.ClearDialog
import com.wooriyo.pinmenumobileer.common.dialog.ConfirmDialog
import com.wooriyo.pinmenumobileer.databinding.ActivityOrderListBinding
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.model.CallHistoryDTO
import com.wooriyo.pinmenumobileer.model.CallListDTO
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import com.wooriyo.pinmenumobileer.util.AppHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

// 호출 목록 확인 페이지 ( 주문 목록 페이지와 레이아웃 같이 사용)
class CallListActivity : BaseActivity() {
    lateinit var binding: ActivityOrderListBinding
    lateinit var clearDialog: ClearDialog
    lateinit var clearConfirmDialog: ConfirmDialog

    val callHistory = ArrayList<CallHistoryDTO>()
    val callListAdapter = CallListAdapter(callHistory)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // OrderList와 layout 같이 쓰기 때문에 Call에 맞게 뷰 변경
        binding.empty.text = getString(R.string.no_call_list)

        // 호출어댑터 리스너 설정 (완료 버튼 눌렀을 때 position 가져오기)
        callListAdapter.setOnItemClickListener(object : ItemClickListener{
            override fun onItemClick(position: Int) { setComplete(position) }
        })
        // 리사이클러뷰 초기화 및 어댑터 연결
        binding.rv.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.rv.adapter = callListAdapter

        setClearDialog()

        // 클릭 이벤트
        binding.back.setOnClickListener{ // 뒤로가기 > 매장 밖으로 나가기 때문에 이용자수 차감 Api 태움
            AppHelper.leaveStore(mActivity)
        }

        binding.btnClear.setOnClickListener { clearDialog.show(supportFragmentManager, "ClearDialog") }
    }

    override fun onResume() {
        super.onResume()
        getCallList()
    }

    // 초기화 / 초기화 확인 다이얼로그 초기화
    fun setClearDialog() {
//        clearDialog = ClearDialog("call", View.OnClickListener {
//            clearDialog.dismiss()
//            clearConfirmDialog.show(supportFragmentManager, "ClearConfirmDialog")
//        })
//        clearConfirmDialog = ConfirmDialog(
//            getString(R.string.dialog_call_clear_title),
//            getString(R.string.dialog_confrim_clear),
//            getString(R.string.btn_confirm),
//            View.OnClickListener {
//                clearConfirmDialog.dismiss()
//                clear()
//            }
//        )
    }

    // 호출 리스트 (히스토리) 조회
    fun getCallList() {
        ApiClient.service.getCallHistory(useridx, storeidx).enqueue(object: Callback<CallListDTO>{
            override fun onResponse(call: Call<CallListDTO>, response: Response<CallListDTO>) {
                Log.d(TAG, "호출 목록 조회 url : $response")
                if(!response.isSuccessful) return

                val result = response.body()
                if(result != null) {
                    when(result.status){
                        1 -> {
                            callHistory.clear()
                            callHistory.addAll(result.callList)

                            if(callHistory.isEmpty()) {
                                binding.empty.visibility = View.VISIBLE
                                binding.rv.visibility = View.GONE
                            }else {
//                                callHistory.sortBy { it.iscompleted }
                                binding.empty.visibility = View.GONE
                                binding.rv.visibility = View.VISIBLE
                                callListAdapter.notifyDataSetChanged()
                            }
                        }
                        else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<CallListDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "호출 목록 조회 오류 > $t")
                Log.d(TAG, "호출 목록 조회 오류 > ${call.request()}")
            }
        })
    }

    // 호출 완료 처리
    fun setComplete(position: Int) {
        ApiClient.service.completeCall(storeidx, callHistory[position].idx, "Y")
            .enqueue(object : Callback<ResultDTO> {
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "호출 완료 url : $response")
                    if (!response.isSuccessful) return

                    val result = response.body() ?: return
                    when (result.status) {
                        1 -> {
                            callHistory[position].iscompleted = 1
//                            callHistory.sortBy { it.iscompleted }

                            callListAdapter.notifyItemChanged(position)
                        }
                        else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "호출 완료 실패 > $t")
                    Log.d(TAG, "호출 완료 실패 오류 > ${call.request()}")
                }
            })
    }

    // 호출 초기화
    fun clear() {
        ApiClient.service.clearCall(useridx, storeidx).enqueue(object:Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "직원호출 초기화 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                if(result.status == 1){
                    getCallList()
                }
            }
            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "직원호출 초기화 실패 > $t")
                Log.d(TAG, "직원호출 초기화 실패 > ${call.request()}")
            }
        })
    }
}