package com.wooriyo.pinmenumobileer.order.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.annotations.SerializedName
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.common.dialog.AlertDialog
import com.wooriyo.pinmenumobileer.databinding.ListReservationBinding
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.model.OrderHistoryDTO
import com.wooriyo.pinmenumobileer.model.ReservationDTO
import com.wooriyo.pinmenumobileer.util.AppHelper

class ReservationAdapter(val dataSet: ArrayList<OrderHistoryDTO>): RecyclerView.Adapter<ReservationAdapter.ViewHolder>() {
    lateinit var completeListener: ItemClickListener
    lateinit var deleteListener: ItemClickListener
    lateinit var printClickListener: ItemClickListener

    fun setOnCompleteListener(completeListener: ItemClickListener) {
        this.completeListener = completeListener
    }

    fun setOnDeleteListener(deleteListener: ItemClickListener) {
        this.deleteListener = deleteListener
    }

    fun setOnPrintClickListener(printClickListener: ItemClickListener) {
        this.printClickListener = printClickListener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListReservationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.rv.layoutManager = LinearLayoutManager(parent.context, LinearLayoutManager.VERTICAL, false)
        return ViewHolder(binding, parent.context, completeListener, deleteListener, printClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val binding: ListReservationBinding, val context: Context, val completeListener: ItemClickListener, val deleteListener: ItemClickListener, val printClickListener: ItemClickListener): RecyclerView.ViewHolder(binding.root) {
        fun bind (data : OrderHistoryDTO) {
            binding.run {
                when(data.reserType) {
                    0 -> {
                        reservType.visibility = View.GONE
                    }
                    1 -> {
                        reservType.visibility = View.VISIBLE
                        reservType.setBackgroundColor(Color.parseColor("#FF005E"))
                        reservType.text = context.getString(R.string.reserv_store)
                        tvDate.text = String.format(context.getString(R.string.reserv_date), "매장")
                    }
                    2 -> {
                        reservType.visibility = View.VISIBLE
                        reservType.setBackgroundColor(Color.parseColor("#46B6FF"))
                        reservType.text = context.getString(R.string.reserv_togo)
                        tvDate.text = String.format(context.getString(R.string.reserv_date), "포장")
                    }
                }

                if(data.rlist.isNotEmpty()) {
                    val rsv = data.rlist[0]
                    reservTel.text = rsv.tel
                    reservName.text = rsv.name
                    reservRequest.text = rsv.memo
                    reservAddr.text = rsv.addr
                    reservDate.text = rsv.reserdt
                }

                rv.adapter = OrderDetailAdapter(data.olist)

                tableNo.text = data.tableNo
                regdt.text = data.regdt
                orderNo.text = data.ordcode
//                gea.text = data.total.toString()
                price.text = AppHelper.price(data.amount)

                if(data.iscompleted == 1) {
                    top.setBackgroundColor(Color.parseColor("#E0E0E0"))
                    clPrice.setBackgroundResource(R.drawable.bg_r6g)
                    btnComplete.setBackgroundResource(R.drawable.bg_r6g)
                    btnComplete.text = "복원"
                    complete.visibility = View.VISIBLE
                    completeQr.visibility = View.GONE
                    completePos.visibility = View.GONE
                } else if (data.paytype == 4) {
                    top.setBackgroundResource(R.color.main)
                    clPrice.setBackgroundResource(R.drawable.bg_r6y)
                    btnComplete.setBackgroundResource(R.drawable.bg_r6y)
                    btnComplete.text = "완료"
                    complete.visibility = View.GONE
                    completeQr.visibility = View.GONE
                    completePos.visibility = View.VISIBLE
                } else if (data.isreser != 0) {

                } else {
                    top.setBackgroundResource(R.color.main)
                    clPrice.setBackgroundResource(R.drawable.bg_r6y)
                    btnComplete.setBackgroundResource(R.drawable.bg_r6y)
                    btnComplete.text = "완료"
                    complete.visibility = View.GONE
                    completeQr.visibility = View.GONE
                    completePos.visibility = View.GONE
                }

                if(data.isreser == 0) {
                    binding.btnComplete.text = context.getString(R.string.confirm)
                }
                else  {
                    top.setBackgroundResource(R.color.main)
                    clPrice.setBackgroundResource(R.drawable.bg_r6g)
                    btnComplete.setBackgroundResource(R.drawable.bg_r6y)
                }

                delete.setOnClickListener { deleteListener.onItemClick(adapterPosition) }
                print.setOnClickListener {
                    if(MyApplication.bluetoothPort.isConnected) {
                        printClickListener.onItemClick(adapterPosition)
                    }else{
                        val fragmentActivity = context as FragmentActivity
                        AlertDialog("", context.getString(R.string.dialog_no_printer)).show(fragmentActivity.supportFragmentManager, "AlertDialog")
                    }
                }
                btnComplete.setOnClickListener { completeListener.onItemClick(adapterPosition) }
            }
        }
    }
}