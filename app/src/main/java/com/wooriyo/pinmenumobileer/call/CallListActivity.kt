package com.wooriyo.pinmenumobileer.call

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication.Companion.store
import com.wooriyo.pinmenumobileer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenumobileer.MyApplication.Companion.useridx
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.call.adapter.CallListAdapter
import com.wooriyo.pinmenumobileer.databinding.ActivityOrderListBinding
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.model.CallHistoryDTO
import com.wooriyo.pinmenumobileer.model.CallListDTO
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

// 호출 목록 확인 페이지 ( 주문 목록 페이지와 레이아웃 같이 사용)
class CallListActivity : BaseActivity() {
    lateinit var binding: ActivityOrderListBinding
    lateinit var timer: Timer

    val TAG = "CallListActivity"
    val mActivity = this@CallListActivity

    val callHistory = ArrayList<CallHistoryDTO>()
    val callListAdapter = CallListAdapter(callHistory)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // OrderList와 layout 같이 쓰기 때문에 Call에 맞게 뷰 변경
        binding.title.text = getString(R.string.call_emp)
        binding.tv.visibility = View.GONE
        binding.today.visibility = View.GONE

        // 호출어댑터 리스너 설정 (완료 버튼 눌렀을 때 position 가져오기)
        callListAdapter.setOnItemClickListener(object : ItemClickListener{
            override fun onItemClick(position: Int) { setComplete(position) }
        })
        // 리사이클러뷰 초기화 및 어댑터 연결
        binding.rv.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.rv.adapter = callListAdapter

        // 클릭 이벤트
        binding.back.setOnClickListener{finish()}

        getCallList()
    }

    override fun onResume() {
        super.onResume()
        timer = Timer()
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                getCallStatus()
            }
        }
        timer.schedule(timerTask, 0, 3000)
    }

    override fun onPause() {
        super.onPause()
        timer.cancel()
    }


    // 새로운 호출 유무 확인 > 3초마다 한번씩 태우기
    fun getCallStatus() {
        ApiClient.service.getCallStatus(useridx, storeidx).enqueue(object : Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "새로운 호출 유무 확인 url : $response")
                if(!response.isSuccessful) return

                val result = response.body()
                if(result != null && result.status == 1) {
                    getCallList()
                    // 음악 재생
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "새로운 호출 유무 확인 실패 > $t")
                Log.d(TAG, "새로운 호출 유무 확인 실패 > ${call.request()}")
            }
        })
    }

    // 호출 확인 처리 > 화면 터치하면
    fun udtCallStatus() {
        ApiClient.service.udtCallStatus(useridx, storeidx).enqueue(object: Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "호출 확인 처리(상태 업데이트) url : $response")
                if(!response.isSuccessful) return

                val result = response.body()
                if(result != null) {
                    when(result.status) {
                        1 -> {
                            // 알림음 종료 등등
                        }
                        else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "호출 확인 처리(상태 업데이트) 실패 > $t")
                Log.d(TAG, "호출 확인 처리(상태 업데이트) 실패 > ${call.request()}")
            }
        })
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
                            callListAdapter.notifyDataSetChanged()
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
        ApiClient.service.completeCall(storeidx, callHistory[position].idx, "Y").enqueue(object:Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "호출 완료 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status){
                    1 -> {
                        callHistory[position].iscompleted = 1
                        callHistory.sortBy {
                            it.iscompleted
                            it.regDt
                        }
                        callListAdapter.notifyDataSetChanged()
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
}