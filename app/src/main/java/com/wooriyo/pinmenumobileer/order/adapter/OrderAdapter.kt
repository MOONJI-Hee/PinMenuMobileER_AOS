package com.wooriyo.pinmenumobileer.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenumobileer.databinding.ListCallBinding
import com.wooriyo.pinmenumobileer.databinding.ListOrderBinding
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.model.OrderHistoryDTO
import com.wooriyo.pinmenumobileer.util.AppHelper

class OrderAdapter(val dataSet: ArrayList<OrderHistoryDTO>): RecyclerView.Adapter<OrderAdapter.ViewHolder>() {
    lateinit var cmpltClickListener: ItemClickListener

    fun setOnCmpltClickListener(cmpltClickListener: ItemClickListener) {
        this.cmpltClickListener = cmpltClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.rv.layoutManager = LinearLayoutManager(parent.context, LinearLayoutManager.VERTICAL, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position], cmpltClickListener)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val binding: ListOrderBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind (data : OrderHistoryDTO, cmpltClickListener : ItemClickListener) {
            binding.rv.adapter = OrderDetailAdapter(data.olist)

            binding.tableNo.text = data.tableNo
            binding.totPrice.text = AppHelper.price(data.amount)

            val date = data.regdt.split(" ")[0].replace("-", ".")
            val time = data.regdt.split(" ")[1].substring(0, 5)

            binding.date.text = date
            binding.time.text = time

            binding.complete.setOnClickListener { cmpltClickListener.onItemClick(absoluteAdapterPosition) }
        }

    }
}