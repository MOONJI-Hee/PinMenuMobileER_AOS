package com.wooriyo.pinmenumobileer.order

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenumobileer.MyApplication.Companion.useridx
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ActivityOrderListBinding
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.model.OrderHistoryDTO
import com.wooriyo.pinmenumobileer.model.OrderListDTO
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.order.adapter.OrderAdapter
import com.wooriyo.pinmenumobileer.util.ApiClient
import com.wooriyo.pinmenumobileer.util.AppHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderListActivity : BaseActivity() {
    lateinit var binding: ActivityOrderListBinding
//    lateinit var timer: Timer

    // 결제 관련 변수
    var payPosition = -1
    var tran_type = "credit"


    val TAG = "OrderListActivity"
    val mActivity = this@OrderListActivity

    val orderList = ArrayList<OrderHistoryDTO>()
    val orderAdapter = OrderAdapter(orderList)

    val goKICC = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            //직전거래에 대한 취소거래필요정보를 받음
            val cancelInfo: Intent = it.data ?: return@registerForActivityResult

            complete()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderAdapter.setOnPayClickListener(object: ItemClickListener{
            override fun onItemClick(position: Int) {
                payPosition = position
                payOrder()
            }
        })

        orderAdapter.setOnDeleteListener(object: ItemClickListener{
            override fun onItemClick(position: Int) { delete(position) }
        })

        binding.rv.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.rv.adapter = orderAdapter

        binding.back.setOnClickListener { AppHelper.leaveStore(mActivity) }

        getOrderList()
    }

    override fun onResume() {
        super.onResume()
//        timer = Timer()
//        val timerTask = object : TimerTask(){
//            override fun run() {
//                getOrdStatus()
//            }
//        }
//        timer.schedule(timerTask, 0, 3000)
    }

    override fun onPause() {
        super.onPause()
//        timer.cancel()
    }

    // 새로운 주문 유무 확인 > 3초마다 한번씩 태우기
    fun getOrdStatus() {
        ApiClient.service.getOrdStatus(useridx, storeidx).enqueue(object : Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "새로운 주문 유무 확인 url : $response")
                if(!response.isSuccessful) return

                val result = response.body()
                if(result != null && result.status == 1) {
                    getOrderList()
                    // 음악 재생
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "새로운 주문 유무 확인 실패 > $t")
                Log.d(TAG, "새로운 주문 유무 확인 실패 > ${call.request()}")
            }
        })
    }

    // 주문 확인 처리 > 화면 터치하면
    fun udtOrdStatus() {
        ApiClient.service.udtOrdStatus(useridx, storeidx).enqueue(object: Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "주문 확인 처리(상태 업데이트) url : $response")
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
                Log.d(TAG, "주문 확인 처리(상태 업데이트) 실패 > $t")
                Log.d(TAG, "주문 확인 처리(상태 업데이트) 실패 > ${call.request()}")
            }
        })
    }

    // 주문 목록 조회
    fun getOrderList() {
        ApiClient.service.getOrderList(useridx, storeidx).enqueue(object : Callback<OrderListDTO>{
            override fun onResponse(call: Call<OrderListDTO>, response: Response<OrderListDTO>) {
                Log.d(TAG, "주문 목록 조회 url : $response")
                if(!response.isSuccessful) return

                val result = response.body()
                if(result != null) {
                    when(result.status){
                        1 -> {
                            orderList.clear()
                            orderList.addAll(result.orderlist)

                            if(orderList.isEmpty()) {
                                binding.empty.visibility = View.VISIBLE
                                binding.rv.visibility = View.GONE
                            }else {
                                orderList.sortBy { it.iscompleted }
                                binding.today.text = orderList.size.toString()
                                binding.empty.visibility = View.GONE
                                binding.rv.visibility = View.VISIBLE
                                orderAdapter.notifyDataSetChanged()
                            }
                        }
                        else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<OrderListDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "주문 목록 조회 오류 > $t")
                Log.d(TAG, "주문 목록 조회 오류 > ${call.request()}")
            }
        })
    }

    // 주문 결제 (KICC)
    fun payOrder() {
        val compName = ComponentName("kr.co.kicc.ectablet", "kr.co.kicc.ectablet.SmartCcmsMain")

        val intent = Intent(Intent.ACTION_MAIN)

//        intent.putExtra("TRAN_NO", "1")
        intent.putExtra("APPCALL_TRAN_NO", AppHelper.getAppCallNo())
        intent.putExtra("TRAN_TYPE", tran_type)
        intent.putExtra("TOTAL_AMOUNT", orderList[payPosition].amount)
//        intent.putExtra("TAX", )  부과세
//        intent.putExtra("TIP", )  팁
//        intent.putExtra("RECEIPT_EMAIL", this.receipt_email.getText().toString())
//        intent.putExtra("RECEIPT_SMS", this.receipt_sms.getText().toString())
        intent.putExtra("INSTALLMENT", 0)

        var signFlag = "N"
        if(orderList[payPosition].amount >= 50000) signFlag = "Y"
        intent.putExtra("SIGN_FLAG", signFlag)

//        intent.putExtra("KEYIN_FLAG", this.keyin_flag.getText().toString())
        intent.putExtra("PRINT_OPTION", this.print_option.getText().toString())
        intent.putExtra("EXTENSION_OPTION", this.extension_option.getText().toString())
        intent.putExtra("UI_SKIP_OPTION", this.uiskip_option.getText().toString())
        intent.putExtra("TOKEN_DATA", this.token_data.getText().toString())
        intent.putExtra("TOKEN_VALID", this.token_valid.getText().toString())
        if (multi) { //다중사업자이면 아래 정보가 필요합니다.
            intent.putExtra("SHOP_TID", this.tid.getText().toString())
            intent.putExtra("SHOP_BIZ_NUM", this.biznum.getText().toString())
            intent.putExtra("SHOP_NAME", this.name.getText().toString())
            intent.putExtra("SHOP_OWNER", this.owner.getText().toString())
            intent.putExtra("SHOP_ADDRESS", this.address.getText().toString())
            intent.putExtra("SHOP_TEL", this.tel.getText().toString())
        }

        if (input_addfield) { //추가필드를 사용하면 데이터 넣어주고
            intent.putExtra("ADD_FIELD", this.add_field.getText().toString())
        } else { //추가필드 사용하지 않으면 주문번호와 고객코드를 넣어주고
            intent.putExtra("ORDER_NUM", this.order_num.getText().toString())
            intent.putExtra("CUSTOMER_CODE", this.customer_code.getText().toString())
        }
        //거래일련번호 취소인 경우 거래일련번호 입력
        //거래일련번호 취소인 경우 거래일련번호 입력
        if (("credit_cancel" == tran_type || "p_cash_cancel" == tran_type || "c_cash_cancel" == tran_type || "v_cash_cancel" == tran_type) && input_transerialno) {
            intent.putExtra("TRAN_SERIALNO", this.tran_serialno.getText().toString())
        }
        //취소거래는 원승인번호, 원승인일자 입력
        //취소거래는 원승인번호, 원승인일자 입력
        if ("credit_cancel" == tran_type || "p_cash_cancel" == tran_type || "c_cash_cancel" == tran_type || "v_cash_cancel" == tran_type || "history" == tran_type || "barcode_cancel" == tran_type) {
            intent.putExtra("APPROVAL_NUM", this.appr_num.getText().toString())
            intent.putExtra("APPROVAL_DATE", this.appr_date.getText().toString())
        }

        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.component = compName

        goKICC.launch(intent)
    }

    // 주문 완료 처리 (결제)
    fun complete() {
        ApiClient.service.payOrder(storeidx, orderList[payPosition].idx ,"Y").enqueue(object:Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "주문 완료 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status){
                    1 -> {
                        orderList[payPosition].iscompleted = 1
                        orderList.sortBy { it.iscompleted }
                        orderAdapter.notifyItemChanged(payPosition)
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "주문 완료 실패 > $t")
                Log.d(TAG, "주문 완료 실패 > ${call.request()}")
            }
        })
    }

    // 주문 삭제
    fun delete(position: Int) {
        ApiClient.service.deleteOrder(storeidx, orderList[position].idx).enqueue(object:Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "주문 삭제 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status){
                    1 -> {
                        Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                        orderList.removeAt(position)
                        orderAdapter.notifyItemRemoved(position)
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "주문 삭제 실패 > $t")
                Log.d(TAG, "주문 삭제 실패 > ${call.request()}")
            }
        })
    }

    // 주문 프린트
    fun print(position: Int) {

    }
}