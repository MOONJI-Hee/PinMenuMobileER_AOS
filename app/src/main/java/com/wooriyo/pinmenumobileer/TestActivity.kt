package com.wooriyo.pinmenumobileer

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.wooriyo.pinmenumobileer.config.AppProperties.Companion.KPN_API_ID_ORDER
import com.wooriyo.pinmenumobileer.config.AppProperties.Companion.KPN_ORDER_TYPE_TABLE
import com.wooriyo.pinmenumobileer.config.AppProperties.Companion.KPN_SERVICE_ID_ORDER
import com.wooriyo.pinmenumobileer.config.AppProperties.Companion.KPN_SYSTEM_ID
import com.wooriyo.pinmenumobileer.config.AppProperties.Companion.KPN_SYSTEM_NM
import com.wooriyo.pinmenumobileer.config.AppProperties.Companion.KPN_VENDOR_ID
import com.wooriyo.pinmenumobileer.config.AppProperties.Companion.KPN_VENDOR_NM
import com.wooriyo.pinmenumobileer.databinding.ActivityTestBinding
import com.wooriyo.pinmenumobileer.model.OrderHistoryDTO
import com.wooriyo.pinmenumobileer.model.OrderListDTO
import com.wooriyo.pinmenumobileer.order.adapter.OrderDetailAdapter
import com.wooriyo.pinmenumobileer.pos.model.KpnOrderDTO
import com.wooriyo.pinmenumobileer.pos.model.KpnOrderDetailDTO
import com.wooriyo.pinmenumobileer.pos.model.KpnResultDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import com.wooriyo.pinmenumobileer.util.AppHelper
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TestActivity : BaseActivity() {
    val mActivity = this@TestActivity
    val TAG = "TestActivity"

    lateinit var binding: ActivityTestBinding

    val storeCode = "031823"
    val prodCode = "900014"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getOrderList()
    }

    // 주문 목록 조회
    private fun getOrderList() {
        ApiClient.service.getOrderList(2, 1).enqueue(object :
            Callback<OrderListDTO> {
            override fun onResponse(call: Call<OrderListDTO>, response: Response<OrderListDTO>) {
                Log.d(TAG, "주문 목록 조회 url : $response")
                if(!response.isSuccessful) return

                val result = response.body()
                if(result != null) {
                    when (result.status) {
                        1 -> {
                            if (result.orderlist.isNotEmpty()) {
                                setOrderView(result.orderlist[0])
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

    fun setOrderView(data: OrderHistoryDTO) {
        val gson = Gson()
        binding.run {
            rv.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
            rv.adapter = OrderDetailAdapter(data.olist)

            tableNo.text = data.tableNo
            regdt.text = data.regdt
            gea.text = data.total.toString()
            price.text = AppHelper.price(data.amount)

            print.setOnClickListener {
                // POS에 데이터 보내기

                val orderList = JSONArray()
                for (i in 0 until data.olist.size) {
                    val ord = data.olist[i]
//                    val orderDetail = KpnOrderDetailDTO(
//                        (i + 1).toString(), prodCode, ord.name, ord.toString(), ord.gea.toString(), ord.amount.toString(),
//                        "0", ord.amount.toString(), "0", ""
//                    )

                    val json = JSONObject()

                    json.put("orderDtlNo", (i + 1).toString())
                    json.put("prodCd", prodCode)
                    json.put("prodNm", ord.name)
                    json.put("saleUprc", ord.toString())
                    json.put("orderQty", ord.gea.toString())
                    json.put("saleAmt", ord.amount.toString())
                    json.put("dcAmt", "0")
                    json.put("dcmSaleUprc", ord.amount.toString())
                    json.put("vatAmt", "0")
                    json.put("cookMemo", "")

                    orderList.put(json)
                }

//                val kpnOrder = KpnOrderDTO (
//                    KPN_VENDOR_ID, KPN_VENDOR_NM, KPN_SYSTEM_ID, KPN_SYSTEM_NM, data.ordcode_key, KPN_ORDER_TYPE_TABLE,
//                    "Y", "", "", "", data.orddt, data.olist.size.toString(), orderList,
//                    data.amount.toString(), data.olist.size.toString(), "", "001", ""
//                )

                val json = JSONObject()

                json.put("orderVendorId", KPN_VENDOR_ID)
                json.put("orderVendorNm", KPN_VENDOR_NM)
                json.put("orderSystemId", KPN_SYSTEM_ID)
                json.put("orderSystemNm", KPN_SYSTEM_NM)
                json.put("orderNo", data.ordcode_key)
                json.put("orderType", KPN_ORDER_TYPE_TABLE)
                json.put("saleYn", "Y")
                json.put("pickTime", "")
                json.put("printTime", "")
                json.put("orgOrderNo", "")
                json.put("orderDt", data.orddt)
                json.put("dataCnt", data.olist.size.toString())
                json.put("OrderDetailInfo", orderList)
                json.put("paymentTotAmt", data.amount.toString())
                json.put("paymentCnt", data.olist.size.toString())
                json.put("kitchenMemo", "")
                json.put("tableCd", "001")
                json.put("posOrderNo", "")



                Log.d(TAG, "주문리스트 >>> $json")


                ApiClient.posService().sendFirst(KPN_SERVICE_ID_ORDER, KPN_API_ID_ORDER, storeCode, storeCode, json)
                    .enqueue(object : Callback<KpnResultDTO>{
                        override fun onResponse(call: Call<KpnResultDTO>, response: Response<KpnResultDTO>) {
                            Log.d(TAG, "연결 가능 프린터 리스트 조회 URL : $response")
                            if(!response.isSuccessful) return

                            val result = response.body() ?: return

                            Toast.makeText(mActivity, result.header.resMsg, Toast.LENGTH_SHORT).show()

                            if(result.header.resCd == "0000") {
                                // 성공했을 때 뭐 하지
                            }
                        }

                        override fun onFailure(call: Call<KpnResultDTO>, t: Throwable) {
                            Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                            Log.d(TAG, "주문 포스 전송 오류 >> $t")
                            Log.d(TAG, "주문 포스 전송 오류 >> ${call.request()}")
                        }
                    })

            }
        }
    }

}