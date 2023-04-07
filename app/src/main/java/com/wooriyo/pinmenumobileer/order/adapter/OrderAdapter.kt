package com.wooriyo.pinmenumobileer.order.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ListOrderBinding
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.model.OrderHistoryDTO
import com.wooriyo.pinmenumobileer.util.AppHelper

class OrderAdapter(val dataSet: ArrayList<OrderHistoryDTO>): RecyclerView.Adapter<OrderAdapter.ViewHolder>() {
    lateinit var completeListener: ItemClickListener
    lateinit var deleteListener: ItemClickListener

    fun setOnCompleteListener(completeListener: ItemClickListener) {
        this.completeListener = completeListener
    }

    fun setOnDeleteListener(deleteListener: ItemClickListener) {
        this.deleteListener = deleteListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.rv.layoutManager = LinearLayoutManager(parent.context, LinearLayoutManager.VERTICAL, false)
        return ViewHolder(binding, completeListener, deleteListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val binding: ListOrderBinding, val completeListener: ItemClickListener, val deleteListener: ItemClickListener): RecyclerView.ViewHolder(binding.root) {
        fun bind (data : OrderHistoryDTO) {
            binding.run {
                rv.adapter = OrderDetailAdapter(data.olist)

                tableNo.text = data.tableNo
                regdt.text = data.regdt
                gea.text = data.olist.size.toString()   //TODO api 확인 후 변경
                price.text = AppHelper.price(data.amount)

                if(data.iscompleted == 1) {
                    tableNo.setBackgroundColor(Color.parseColor("#E0E0E0"))
                    done.visibility = View.VISIBLE
                    payment.isEnabled = false
                }else {
                    tableNo.setBackgroundResource(R.color.main)
                    done.visibility = View.GONE
                    payment.isEnabled = true
                }

                delete.setOnClickListener { deleteListener.onItemClick(adapterPosition) }
                print.setOnClickListener {  }
                payment.setOnClickListener { completeListener.onItemClick(adapterPosition) }
            }
        }

    }
}