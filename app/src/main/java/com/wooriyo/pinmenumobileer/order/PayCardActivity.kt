package com.wooriyo.pinmenumobileer.order

import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.common.dialog.ConfirmDialog
import com.wooriyo.pinmenumobileer.databinding.ActivityPayCardBinding
import com.wooriyo.pinmenumobileer.listener.EasyCheckListener
import com.wooriyo.pinmenumobileer.model.OrderHistoryDTO
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.receiver.EasyCheckReceiver
import com.wooriyo.pinmenumobileer.util.ApiClient
import com.wooriyo.pinmenumobileer.util.AppHelper
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PayCardActivity : BaseActivity() {
    val TAG = "PayCardActivity"
    val mActivity = this@PayCardActivity

    lateinit var binding: ActivityPayCardBinding
    lateinit var order : OrderHistoryDTO

    var totGea = 0
    var totPrice = 0
    var charge = 0
    var remain = 0

    lateinit var receiver : EasyCheckReceiver

    val goKICC = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            Log.d(TAG, "결제 성공")
            //직전거래에 대한 취소거래필요정보를 받음
            val cancelInfo: Intent = it.data ?: return@registerForActivityResult
//            val rtn = it.data
//            if(rtn != null) {
//            }
        }
    }

    val selectMenu = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == RESULT_OK) {
            charge = result.data?.getIntExtra("charge", charge) ?: charge
            remain = totPrice - charge
            binding.chargePrice.text = AppHelper.price(charge)
            binding.remainPrice.text = AppHelper.price(remain)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        order = intent.getSerializableExtra("order") as OrderHistoryDTO

        totGea = order.olist.size
        totPrice = order.amount
        charge = totPrice

        binding.tableNo.text = order.tableNo
        binding.date.text = order.regdt

        binding.totGea.text = String.format(getString(R.string.total_gea), totGea)
        binding.totPrice.text = AppHelper.price(totPrice)
        binding.chargePrice.text = AppHelper.price(charge)

        binding.back.setOnClickListener { finish() }
        binding.payment.setOnClickListener { payOrder() }
        binding.selectMenu.setOnClickListener {
            val intent = Intent(mActivity, SelMenuActivity::class.java)
            intent.putExtra("order", order)
            selectMenu.launch(intent)
        }
        binding.done.setOnClickListener {
            val clickListener = View.OnClickListener {
                setResult(RESULT_OK, intent)
                finish()
            }
            val confirmDialog = ConfirmDialog(getString(R.string.dialog_notice), getString(R.string.order_pay_finish_info), getString(R.string.btn_complete), clickListener)
            confirmDialog.show(supportFragmentManager, "ConfirmDialog")
        }

        receiver = EasyCheckReceiver()
        receiver.setOnEasyCheckListener(object : EasyCheckListener {
            override fun getIntent(intent: Intent?) {
                val json = JSONObject()

                val b = intent!!.extras
                val iter: Iterator<String> = b!!.keySet().iterator()
                while (iter.hasNext()) {
                    val key = iter.next()
                    val value = b[key]

                    json.put(key, value)
                }
                insPayCard(json.toString())
            }
        })
        val filter = IntentFilter("kr.co.kicc.ectablet.broadcast")
        this.registerReceiver(receiver, filter)
    }

    // 카드 결제 처리 (KICC 앱으로 이동)
    fun payOrder() {
        val compName = ComponentName("kr.co.kicc.ectablet", "kr.co.kicc.ectablet.SmartCcmsMain")

        val intent = Intent(Intent.ACTION_MAIN)

        intent.putExtra("APPCALL_TRAN_NO", AppHelper.getAppCallNo())
        intent.putExtra("TRAN_TYPE", "credit")
        intent.putExtra("TOTAL_AMOUNT", charge.toString())

        val tax = (charge * 0.1).toInt()
        intent.putExtra("TAX", tax.toString())
        intent.putExtra("TIP", "0")
        intent.putExtra("INSTALLMENT", "0")
        intent.putExtra("UI_SKIP_OPTION", "NNNNN")

        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.component = compName

        try {
            startActivity(intent)
//            goKICC.launch(intent)
        }catch (e: Exception) {
            Toast.makeText(mActivity, R.string.msg_no_card_reader, Toast.LENGTH_SHORT).show()
        }
    }

    // 카드 결제 후 결과 저장
    fun insPayCard(data: String) {
        ApiClient.service.insPayCard(MyApplication.storeidx, data).enqueue(object :
            Callback<ResultDTO> {
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "카드 결제 결과 저장 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status){
                    1 -> {
//                        Toast.makeText(mActivity, R.string.complete, Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "카드 결제 결과 저장 실패 > $t")
                Log.d(TAG, "카드 결제 결과 저장 실패 > ${call.request()}")
            }
        })
    }

}