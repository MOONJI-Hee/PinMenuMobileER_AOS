package com.wooriyo.pinmenumobileer.order.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenumobileer.databinding.ListOrderDetailBinding
import com.wooriyo.pinmenumobileer.model.OrderDTO
import com.wooriyo.pinmenumobileer.util.AppHelper

class OrderDetailAdapter(val dataSet: ArrayList<OrderDTO>): RecyclerView.Adapter<OrderDetailAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListOrderDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val binding: ListOrderDetailBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind (data : OrderDTO) {
            binding.run {
                name.text = data.name
                gea.text = data.gea.toString()
                price.text = AppHelper.price(data.price)

                if(data.opt.isNotEmpty()) {
                    option.text = data.opt.replace(" / ", "\n")
                    option.visibility = View.VISIBLE
                }else
                    option.visibility = View.GONE
            }
        }
    }
}