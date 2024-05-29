package com.wooriyo.pinmenumobileer.history

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.sam4s.printer.Sam4sBuilder
import com.sewoo.jpos.command.ESCPOSConst
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenumobileer.MyApplication.Companion.useridx
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.call.adapter.CallListAdapter
import com.wooriyo.pinmenumobileer.common.dialog.ClearDialog
import com.wooriyo.pinmenumobileer.common.dialog.ConfirmDialog
import com.wooriyo.pinmenumobileer.config.AppProperties
import com.wooriyo.pinmenumobileer.databinding.ActivityOrderListBinding
import com.wooriyo.pinmenumobileer.history.adapter.HistoryAdapter
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.model.CallHistoryDTO
import com.wooriyo.pinmenumobileer.model.CallListDTO
import com.wooriyo.pinmenumobileer.model.OrderDTO
import com.wooriyo.pinmenumobileer.model.OrderHistoryDTO
import com.wooriyo.pinmenumobileer.model.OrderListDTO
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.order.adapter.OrderAdapter
import com.wooriyo.pinmenumobileer.util.ApiClient
import com.wooriyo.pinmenumobileer.util.AppHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ByHistoryActivity: BaseActivity() {
    lateinit var binding: ActivityOrderListBinding

    private val orderList = ArrayList<OrderHistoryDTO>()
    val orderAdapter = OrderAdapter(orderList)

    private val callList = ArrayList<CallHistoryDTO>()
    val callAdapter = CallListAdapter(callList)

    private val completeList = ArrayList<OrderHistoryDTO>()
    val completeAdapter = HistoryAdapter(completeList)

    var selText: TextView?= null

    // 프린트 관련 변수
    var hyphen = StringBuilder()    // 하이픈
    var hyphen_num = 0              // 하이픈 개수
    var font_size = 0
    var hangul_size = 0.0
    var one_line = 0
    var space = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 영수증에 들어가는 하이픈 문자열 초기화, 설정값 초기화
        if(MyApplication.store.fontsize == 2) {
            hyphen_num = AppProperties.HYPHEN_NUM_SMALL
            font_size = AppProperties.FONT_SMALL
            hangul_size = AppProperties.HANGUL_SIZE_SMALL
            one_line = AppProperties.ONE_LINE_SMALL
            space = AppProperties.SPACE_SMALL
        }else if(MyApplication.store.fontsize == 1) {
            hyphen_num = AppProperties.HYPHEN_NUM_BIG
            font_size = AppProperties.FONT_BIG
            hangul_size = AppProperties.HANGUL_SIZE_BIG
            one_line = AppProperties.ONE_LINE_BIG
            space = AppProperties.SPACE_BIG
        }

        for (i in 1..hyphen_num) {
            hyphen.append("-")
        }

        selectTab(binding.tvOrder)

        setAdapterListener(completeAdapter, completeList)
        setOrderAdapter()
        setCallAdapter()

        binding.rv.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.rv.adapter = orderAdapter

        binding.tabOrder.setOnClickListener {
            selectTab(binding.tvOrder)
            binding.rv.adapter = orderAdapter
            getOrderList()
            binding.newOrder.visibility = View.INVISIBLE
        }

        binding.tabCall.setOnClickListener {
            selectTab(binding.tvCall)
            binding.rv.adapter = callAdapter
            getCallList()
            binding.newCall.visibility = View.INVISIBLE
        }

        binding.tabCmplt.setOnClickListener {
            selectTab(binding.tvCmplt)
            binding.rv.adapter = completeAdapter
            getCompletedList()
        }

        binding.btnClear.setOnClickListener {
            ClearDialog({ clearCall() }, { clearOrder() }).show(supportFragmentManager, "ClearDialog")
        }

        binding.back.setOnClickListener { AppHelper.leaveStore(mActivity) }
    }

    override fun onResume() {
        super.onResume()
        reload()
    }

    fun selectTab(tv: TextView) {
        if(selText != tv) {
            selText?.setTextColor(Color.WHITE)
            selText?.setTypeface(null, Typeface.NORMAL)
            tv.setTextColor(ContextCompat.getColor(mActivity, R.color.main))
            tv.setTypeface(tv.typeface, Typeface.BOLD)
            selText = tv
        }
    }

    fun reload() {
        when(selText) {
            binding.tvOrder -> getOrderList()
            binding.tvCall -> getCallList()
            binding.tvCmplt -> getCompletedList()
        }
    }

    fun newOrder() {
        runOnUiThread{
            if(selText == binding.tvOrder) {
                getOrderList()
            }else {
                binding.newOrder.visibility = View.VISIBLE
            }
        }
    }

    fun newCall() {
        runOnUiThread {
            if(selText == binding.tvCall) {
                getCallList()
            }else {
                binding.newCall.visibility = View.VISIBLE
            }
        }
    }

    private fun setAdapterListener(adapter: HistoryAdapter, list: ArrayList<OrderHistoryDTO>) {
        adapter.setOnOrderCompleteListener(object : ItemClickListener{
            override fun onItemClick(position: Int) {
                super.onItemClick(position)

                if(list[position].iscompleted == 0) {
                    showCompleteDialog("주문") { completeOrder(list[position].idx, 1) }
                }else {
                    completeOrder(list[position].idx, 0)
                }
            }
        })

        adapter.setOnDeleteListener(object:ItemClickListener{
            override fun onItemClick(position: Int) {
                ConfirmDialog(
                    getString(R.string.btn_delete),
                    getString(R.string.dialog_delete),
                    getString(R.string.btn_delete)
                ) { deleteOrder(list[position].idx) }.show(supportFragmentManager, "DeleteDialog")
            }
        })

        adapter.setOnPrintClickListener(object:ItemClickListener{
            override fun onItemClick(position: Int) {print(position)}
        })

        adapter.setOnCallCompleteListener(object : ItemClickListener{
            override fun onItemClick(position: Int) {
                super.onItemClick(position)
                showCompleteDialog("호출") { completeCall(list[position].idx) }
            }
        })

        adapter.setOnCallDeleteListener(object : ItemClickListener{
            override fun onItemClick(position: Int) {
                super.onItemClick(position)
                ConfirmDialog(
                    getString(R.string.btn_delete),
                    getString(R.string.dialog_delete),
                    getString(R.string.btn_delete)
                ) { deleteCall(list[position].idx) }.show(supportFragmentManager, "DeleteCallDialog")
            }
        })
    }

    fun setOrderAdapter() {
        orderAdapter.setOnCompleteListener(object : ItemClickListener{
            override fun onItemClick(position: Int) {
                super.onItemClick(position)

                if(orderList[position].iscompleted == 0) {
                    showCompleteDialog("주문") { completeOrder(orderList[position].idx, 1) }
                }else {
                    completeOrder(orderList[position].idx, 0)
                }
            }
        })

        orderAdapter.setOnDeleteListener(object:ItemClickListener{
            override fun onItemClick(position: Int) {
                ConfirmDialog(
                    getString(R.string.btn_delete),
                    getString(R.string.dialog_delete),
                    getString(R.string.btn_delete)
                ) { deleteOrder(orderList[position].idx) }.show(supportFragmentManager, "DeleteDialog")
            }
        })

        orderAdapter.setOnPrintClickListener(object:ItemClickListener{
            override fun onItemClick(position: Int) {print(position)}
        })
    }

    fun setCallAdapter() {
        callAdapter.setOnItemClickListener(object : ItemClickListener{
            override fun onItemClick(position: Int) {
                super.onItemClick(position)
                showCompleteDialog("호출") { completeCall(callList[position].idx) }
            }
        })
        callAdapter.setOnDeleteListener(object : ItemClickListener{
            override fun onItemClick(position: Int) {
                super.onItemClick(position)
                ConfirmDialog(
                    getString(R.string.btn_delete),
                    getString(R.string.dialog_delete),
                    getString(R.string.btn_delete)
                ) { deleteCall(callList[position].idx) }.show(supportFragmentManager, "DeleteCallDialog")
            }
        })
    }

    fun showCompleteDialog(type: String, event: View.OnClickListener) {
        val completeDialog = ConfirmDialog("", String.format(getString(R.string.dialog_complete), type), getString(R.string.btn_complete), event)
        completeDialog.show(supportFragmentManager, "CompleteDialog")
    }

    // 주문 목록 조회
    fun getOrderList() {
        ApiClient.service.getOrderList(useridx, storeidx).enqueue(object: Callback<OrderListDTO> {
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

    // 호출 리스트 (히스토리) 조회
    fun getCallList() {
        ApiClient.service.getCallHistory(useridx, storeidx).enqueue(object: Callback<CallListDTO> {
            override fun onResponse(call: Call<CallListDTO>, response: Response<CallListDTO>) {
                Log.d(TAG, "호출 목록 조회 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return

                if(result.status == 1){
                    callList.clear()
                    callList.addAll(result.callList)

                    if(callList.isEmpty()) {
                        binding.empty.visibility = View.VISIBLE
                        binding.rv.visibility = View.GONE
                    }else {
                        binding.empty.visibility = View.GONE
                        binding.rv.visibility = View.VISIBLE
                        callAdapter.notifyDataSetChanged()
                    }
                } else
                    Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()

            }
            override fun onFailure(call: Call<CallListDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "호출 목록 조회 오류 > $t")
                Log.d(TAG, "호출 목록 조회 오류 > ${call.request()}")
            }
        })
    }

    // 완료 목록 조회
    fun getCompletedList() {
        ApiClient.service.getCompletedList(useridx, storeidx).enqueue(object :
            Callback<OrderListDTO> {
            override fun onResponse(call: Call<OrderListDTO>, response: Response<OrderListDTO>) {
                Log.d(TAG, "완료 목록 조회 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                if(result.status == 1) {
                    completeList.clear()
                    completeList.addAll(result.orderlist)

                    if(completeList.isEmpty()) {
                        binding.empty.visibility = View.VISIBLE
                        binding.rv.visibility = View.GONE
                    }else {
                        binding.empty.visibility = View.GONE
                        binding.rv.visibility = View.VISIBLE
                        completeAdapter.notifyDataSetChanged()
                    }
                }else
                    Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<OrderListDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "완료 목록 조회 오류 > $t")
                Log.d(TAG, "완료 목록 조회 오류 > ${call.request()}")
            }
        })
    }

    // 주문 초기화
    fun clearOrder() {
        ApiClient.service.clearOrder(useridx, storeidx).enqueue(object:Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "주문 초기화 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                if(result.status == 1){
                    if(selText != binding.tvCall) {
                        reload()
                    }
                }
            }
            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "주문 초기화 실패 > $t")
                Log.d(TAG, "주문 초기화 실패 > ${call.request()}")
            }
        })
    }

    // 호출 초기화
    fun clearCall() {
        ApiClient.service.clearCall(useridx, storeidx).enqueue(object:Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "직원호출 초기화 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                if(result.status == 1){
                    if(selText != binding.tvOrder)
                        reload()
                }
            }
            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "직원호출 초기화 실패 > $t")
                Log.d(TAG, "직원호출 초기화 실패 > ${call.request()}")
            }
        })
    }

    // 주문 완료 처리
    fun completeOrder(idx: Int, isCompleted: Int) {
        val status = if(isCompleted == 1) "Y" else "N"
        ApiClient.service.udtComplete(storeidx, idx, status)
            .enqueue(object:Callback<ResultDTO>{
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "주문 완료 url : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return
                    when(result.status){
                        1 -> {
                            Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                            reload()
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

    // 호출 완료 처리
    fun completeCall(idx: Int) {
        ApiClient.service.completeCall(storeidx, idx, "Y").enqueue(object:Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "호출 완료 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status){
                    1 -> {
                        Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                        reload()
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

    // 주문 삭제
    fun deleteOrder(idx: Int) {
        ApiClient.service.deleteOrder(storeidx, idx).enqueue(object:Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "주문 삭제 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status){
                    1 -> {
                        Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                        reload()
//                        orderList.removeAt(position)
//                        orderAdapter.notifyItemRemoved(position)
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

    // 호촐 삭제
    fun deleteCall(idx: Int) {
        ApiClient.service.deleteCall(storeidx, idx).enqueue(object:Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "호출 삭제 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status){
                    1 -> {
                        Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                        reload()
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "호출 삭제 실패 > $t")
                Log.d(TAG, "호출 삭제 실패 > ${call.request()}")
            }
        })
    }

    // 주문 프린트
    fun print(position: Int) {
        val pOrderDt = orderList[position].regdt
        val pTableNo = orderList[position].tableNo
        val pOrderNo = orderList[position].ordcode

        if(MyApplication.bluetoothPort.isConnected){
            MyApplication.escposPrinter.printAndroidFont(
                MyApplication.store.name,
                AppProperties.FONT_WIDTH,
                AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
            MyApplication.escposPrinter.printAndroidFont("주문날짜 : $pOrderDt",
                AppProperties.FONT_WIDTH,
                AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
            MyApplication.escposPrinter.printAndroidFont("주문번호 : $pOrderNo",
                AppProperties.FONT_WIDTH,
                AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
            MyApplication.escposPrinter.printAndroidFont("테이블번호 : $pTableNo",
                AppProperties.FONT_WIDTH,
                AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
            MyApplication.escposPrinter.printAndroidFont(
                AppProperties.TITLE_MENU,
                AppProperties.FONT_WIDTH,
                AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
            MyApplication.escposPrinter.printAndroidFont(hyphen.toString(),
                AppProperties.FONT_WIDTH, font_size, ESCPOSConst.LK_ALIGNMENT_LEFT)

            orderList[position].olist.forEach {
                val pOrder = AppHelper.getPrint(it)
                MyApplication.escposPrinter.printAndroidFont(pOrder,
                    AppProperties.FONT_WIDTH, font_size, ESCPOSConst.LK_ALIGNMENT_LEFT)
            }
            MyApplication.escposPrinter.lineFeed(4)
            MyApplication.escposPrinter.cutPaper()
        }

        //SAM4S
//        MyApplication.cubeBuilder.createCommandBuffer()
//        MyApplication.cubeBuilder.addText("${MyApplication.store.name}\n")
//        MyApplication.cubeBuilder.addText("주문날짜 : $pOrderDt\n")
//        MyApplication.cubeBuilder.addText("주문번호 : $pOrderNo\n")
//        MyApplication.cubeBuilder.addText("테이블번호 : $pTableNo\n")
//        MyApplication.cubeBuilder.addText(AppProperties.TITLE_MENU_SAM4S)
//        MyApplication.cubeBuilder.addText(hyphen.toString())
//
//        orderList[position].olist.forEach {
//            val pOrder = AppHelper.getSam4sPrint(it)
//            MyApplication.cubeBuilder.addText("$pOrder\n")
//        }
//        MyApplication.cubeBuilder.addFeedLine(4)
//        MyApplication.cubeBuilder.addCut(Sam4sBuilder.CUT_NO_FEED)
//
//        //send builder data
//        try {
//            //cl_Menu.mPrinter.sendData(builder);
//            MyApplication.INSTANCE.mPrinterConnection?.sendData(MyApplication.cubeBuilder)
//            MyApplication.cubeBuilder.clearCommandBuffer()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        if(AppHelper.checkCubeConn(mActivity) == 1) {
//
//        }
    }

//    fun getPrint(ord: OrderDTO) : String {
//        var total = 0.0
//
//        val result: StringBuilder = StringBuilder()
//        val underline1 = StringBuilder()
//        val underline2 = StringBuilder()
//
//        ord.name.forEach {
//            if(total < one_line)
//                result.append(it)
//            else if(total < (one_line * 2))
//                underline1.append(it)
//            else
//                underline2.append(it)
//
//            if(it == ' ') {
//                total++
//            }else
//                total += hangul_size
//        }
//
//        val mlength = result.toString().length
//        val mHangul = result.toString().replace(" ", "").length
//        val mSpace = mlength - mHangul
//        val mLine = mHangul * hangul_size + mSpace
//
//        var diff = (one_line - mLine + 0.5).toInt()
//
//        if(MyApplication.store.fontsize == 1) {
//            if(ord.gea < 10) {
//                diff += 1
//                space = 4
//            } else if (ord.gea >= 100) {
//                space = 1
//            }
//        }else if(MyApplication.store.fontsize == 2) {
//            if(ord.gea < 10) {
//                diff += 1
//                space += 2
//            } else if (ord.gea < 100) {
//                space += 1
//            }
//        }
//
//        for(i in 1..diff) {
//            result.append(" ")
//        }
//        result.append(ord.gea.toString())
//
//        for (i in 1..space) {
//            result.append(" ")
//        }
//
//        var togo = ""
//        when(ord.togotype) {
//            1-> togo = "신규"
//            2-> togo = "포장"
//        }
//        result.append(togo)
//
//        if(underline1.toString() != "")
//            result.append("\n$underline1")
//
//        if(underline2.toString() != "")
//            result.append("\n$underline2")
//
//        return result.toString()
//    }
}