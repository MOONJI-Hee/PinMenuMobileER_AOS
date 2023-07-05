package com.wooriyo.pinmenumobileer.order

import android.os.Bundle
import android.widget.CheckBox
import androidx.recyclerview.widget.LinearLayoutManager
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.databinding.ActivitySelMenuBinding
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.model.OrderDTO
import com.wooriyo.pinmenumobileer.model.OrderHistoryDTO
import com.wooriyo.pinmenumobileer.order.adapter.OrderPayDetailAdapter
import com.wooriyo.pinmenumobileer.util.AppHelper

class SelMenuActivity : BaseActivity() {
    lateinit var binding: ActivitySelMenuBinding

    val TAG = "SelMenuActivity"
    val mActivity = this@SelMenuActivity

    lateinit var order : OrderHistoryDTO
    lateinit var olist : ArrayList<OrderDTO>
    lateinit var adapter: OrderPayDetailAdapter

    var checkedAll = true

    var charge = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        order = intent.getSerializableExtra("order") as OrderHistoryDTO
        olist = order.olist
        olist.forEach { it.isChecked = true }

        charge = order.amount
        binding.chargePrice.text = AppHelper.price(charge)

        adapter = OrderPayDetailAdapter(olist)
        adapter.setOnCheckListener(object : ItemClickListener {
            override fun onCheckClick(position: Int, v: CheckBox, isChecked: Boolean) {
                val oPrice = olist[position].price * olist[position].gea

                if(isChecked) {
                    charge += oPrice
                }else {
                    charge -= oPrice
                }

                olist[position].isChecked = isChecked

                binding.chargePrice.text = AppHelper.price(charge)

                olist.forEach {
                    if(!it.isChecked) {
                        binding.checkAll.isChecked = false
                        return
                    }
                }
                binding.checkAll.isChecked = true
            }
        })

        binding.rv.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        binding.rv.adapter = adapter

        binding.tableNo.text = order.tableNo
        binding.regdt.text = order.regdt

        binding.back.setOnClickListener { finish() }
        binding.checkAll.setOnClickListener {
            it as CheckBox
            if(it.isChecked) {
                checkedAll = true
                for (orderDTO in order.olist) {
                    orderDTO.isChecked = true
                }
            }else {
                checkedAll = false
                for (orderDTO in order.olist) {
                    orderDTO.isChecked = false
                }
            }
            adapter.notifyDataSetChanged()
        }
        binding.select.setOnClickListener {
            intent.putExtra("charge", charge)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}