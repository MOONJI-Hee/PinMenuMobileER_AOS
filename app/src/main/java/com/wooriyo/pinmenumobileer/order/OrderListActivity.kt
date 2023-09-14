package com.wooriyo.pinmenumobileer.order

import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.sewoo.jpos.command.ESCPOSConst
import com.sewoo.jpos.printer.ESCPOSPrinter
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication.Companion.store
import com.wooriyo.pinmenumobileer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenumobileer.MyApplication.Companion.useridx
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.config.AppProperties
import com.wooriyo.pinmenumobileer.config.AppProperties.Companion.FONT_BIG
import com.wooriyo.pinmenumobileer.config.AppProperties.Companion.FONT_SMALL
import com.wooriyo.pinmenumobileer.config.AppProperties.Companion.FONT_WIDTH
import com.wooriyo.pinmenumobileer.config.AppProperties.Companion.HANGUL_SIZE_BIG
import com.wooriyo.pinmenumobileer.config.AppProperties.Companion.HANGUL_SIZE_SMALL
import com.wooriyo.pinmenumobileer.config.AppProperties.Companion.ONE_LINE_BIG
import com.wooriyo.pinmenumobileer.config.AppProperties.Companion.ONE_LINE_SMALL
import com.wooriyo.pinmenumobileer.config.AppProperties.Companion.SPACE_BIG
import com.wooriyo.pinmenumobileer.config.AppProperties.Companion.SPACE_SMALL
import com.wooriyo.pinmenumobileer.config.AppProperties.Companion.TITLE_MENU
import com.wooriyo.pinmenumobileer.databinding.ActivityOrderListBinding
import com.wooriyo.pinmenumobileer.listener.DialogListener
import com.wooriyo.pinmenumobileer.listener.EasyCheckListener
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.model.OrderDTO
import com.wooriyo.pinmenumobileer.model.OrderHistoryDTO
import com.wooriyo.pinmenumobileer.model.OrderListDTO
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.order.adapter.OrderAdapter
import com.wooriyo.pinmenumobileer.order.dialog.CompleteDialog
import com.wooriyo.pinmenumobileer.order.dialog.SelectPayDialog
import com.wooriyo.pinmenumobileer.payment.NicepayInfoActivity
import com.wooriyo.pinmenumobileer.payment.QrActivity
import com.wooriyo.pinmenumobileer.receiver.EasyCheckReceiver
import com.wooriyo.pinmenumobileer.util.ApiClient
import com.wooriyo.pinmenumobileer.util.AppHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OrderListActivity : BaseActivity() {
    lateinit var binding: ActivityOrderListBinding

    val TAG = "OrderListActivity"
    val mActivity = this@OrderListActivity

    val orderList = ArrayList<OrderHistoryDTO>()
    val orderAdapter = OrderAdapter(orderList)

    // 프린트 관련 변수 > MyApplication에 선언함
    val escposPrinter = ESCPOSPrinter()

    var hyphen = StringBuilder()    // 하이픈
    var hyphen_num = 0              // 하이픈 개수
    var font_size = 0
    var hangul_size = 0.0
    var one_line = 0
    var space = 0

    // 결제 관련 변수
    var payPosition = -1

    val paymentCard = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == RESULT_OK) {
            orderList[payPosition].iscompleted = 1
            orderList.sortBy { it.iscompleted }
            orderAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 영수증에 들어가는 하이픈 문자열 초기화, 설정값 초기화
        if(store.fontsize == 2) {
            hyphen_num = AppProperties.HYPHEN_NUM_SMALL
            font_size = FONT_SMALL
            hangul_size = HANGUL_SIZE_SMALL
            one_line = ONE_LINE_SMALL
            space = SPACE_SMALL
        }else if(store.fontsize == 1) {
            hyphen_num = AppProperties.HYPHEN_NUM_BIG
            font_size = FONT_BIG
            hangul_size = HANGUL_SIZE_BIG
            one_line = ONE_LINE_BIG
            space = SPACE_BIG
        }
        for (i in 1..hyphen_num) {
            hyphen.append("-")
        }

        orderAdapter.setOnPayClickListener(object: ItemClickListener{
            override fun onItemClick(position: Int) {
                val selectPayDialog = SelectPayDialog(position)

                selectPayDialog.setOnQrClickListener(object : ItemClickListener{
                    override fun onQrClick(position: Int, status: Boolean) {
                        super.onQrClick(position, status)
                        if(status) {
                            val intent = Intent(mActivity, QrActivity::class.java)
                            intent.putExtra("ordcode", orderList[position].ordcode)
                            startActivity(intent)
                        }else {
                            val intent = Intent(mActivity, NicepayInfoActivity::class.java)
                            intent.putExtra("fromOrder", "Y")
                            startActivity(intent)
                        }
                    }
                })

                selectPayDialog.setOnCardClickListener(object : ItemClickListener{
                    override fun onItemClick(position: Int) {
                        super.onItemClick(position)
                        selectPayDialog.dismiss()
                        payPosition = position

                        val intent = Intent(mActivity, PayCardActivity::class.java)
                        intent.putExtra("order", orderList[payPosition])
                        paymentCard.launch(intent)
                    }
                })

                selectPayDialog.setOnCompleteClickListener(object : ItemClickListener{
                    override fun onItemClick(position: Int) {
                        super.onItemClick(position)
                        when(store.popup) {
                            0 -> {
                                val dialog = CompleteDialog()
                                dialog.setOnCompleteListener(object : DialogListener {
                                    override fun onComplete(popup: Int) {
                                        super.onComplete(popup)
                                        complete(position, popup)
                                        dialog.dismiss()
                                    }
                                })
                                selectPayDialog.dismiss()
                                dialog.show(supportFragmentManager, "CompleteDailog")
                            }
                            1 ->  {
                                complete(position, store.popup)
                                selectPayDialog.dismiss()
                            }
                        }
                    }
                })

                selectPayDialog.show(supportFragmentManager, "PayDialog")
            }
        })
        orderAdapter.setOnDeleteListener(object:ItemClickListener{
            override fun onItemClick(position: Int) {delete(position)}
        })

        orderAdapter.setOnPrintClickListener(object:ItemClickListener{
            override fun onItemClick(position: Int) {print(position)}
        })


        binding.rv.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.rv.adapter = orderAdapter

        binding.back.setOnClickListener { AppHelper.leaveStore(mActivity) }
    }

    override fun onResume() {
        super.onResume()
        getOrderList()
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
                                binding.total.text = result.totalCnt.toString()
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

    // 주문 완료 처리
    fun complete(position: Int, popup: Int) {
        ApiClient.service.udtComplete(storeidx, orderList[position].idx ,"Y", popup).enqueue(object:Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "주문 완료 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status){
                    1 -> {
                        Toast.makeText(mActivity, R.string.complete, Toast.LENGTH_SHORT).show()
                        orderList[position].iscompleted = 1
                        orderList.sortBy { it.iscompleted }
                        orderAdapter.notifyItemChanged(position)
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
        val pOrderDt = orderList[position].regdt
        val pTableNo = orderList[position].tableNo
        val pOrderNo = orderList[position].ordcode

        escposPrinter.printAndroidFont(store.name, FONT_WIDTH, FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
        escposPrinter.printAndroidFont("주문날짜 : $pOrderDt", FONT_WIDTH, FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
        escposPrinter.printAndroidFont("주문번호 : $pOrderNo", FONT_WIDTH, FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
        escposPrinter.printAndroidFont("테이블번호 : $pTableNo", FONT_WIDTH, FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
        escposPrinter.printAndroidFont(TITLE_MENU, FONT_WIDTH, FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
        escposPrinter.printAndroidFont(hyphen.toString(), FONT_WIDTH, font_size, ESCPOSConst.LK_ALIGNMENT_LEFT)

        orderList[position].olist.forEach {
            val pOrder = getPrint(it)
            escposPrinter.printAndroidFont(pOrder, FONT_WIDTH, font_size, ESCPOSConst.LK_ALIGNMENT_LEFT)
        }
        escposPrinter.lineFeed(4)
        escposPrinter.cutPaper()
    }

    fun getPrint(ord: OrderDTO) : String {
        var total = 0.0

        val result: StringBuilder = StringBuilder()
        val underline1 = StringBuilder()
        val underline2 = StringBuilder()

        ord.name.forEach {
            if(total < one_line)
                result.append(it)
            else if(total < (one_line * 2))
                underline1.append(it)
            else
                underline2.append(it)

            if(it == ' ') {
                total++
            }else
                total += hangul_size
        }

        val mlength = result.toString().length
        val mHangul = result.toString().replace(" ", "").length
        val mSpace = mlength - mHangul
        val mLine = mHangul * hangul_size + mSpace

        var diff = (one_line - mLine + 0.5).toInt()

        if(store.fontsize == 1) {
            if(ord.gea < 10) {
                diff += 1
                space = 4
            } else if (ord.gea >= 100) {
                space = 1
            }
        }else if(store.fontsize == 2) {
            if(ord.gea < 10) {
                diff += 1
                space += 2
            } else if (ord.gea < 100) {
                space += 1
            }
        }

        for(i in 1..diff) {
            result.append(" ")
        }
        result.append(ord.gea.toString())

        for (i in 1..space) {
            result.append(" ")
        }

        var togo = ""
        when(ord.togotype) {
            1-> togo = "신규"
            2-> togo = "포장"
        }
        result.append(togo)

        if(underline1.toString() != "")
            result.append("\n$underline1")

        if(underline2.toString() != "")
            result.append("\n$underline2")

        return result.toString()
    }
}