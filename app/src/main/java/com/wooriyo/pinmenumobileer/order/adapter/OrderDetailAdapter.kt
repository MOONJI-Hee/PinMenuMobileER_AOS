package com.wooriyo.pinmenumobileer.order.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenumobileer.databinding.ListOrderDetailBinding
import com.wooriyo.pinmenumobileer.model.OrderDTO
import com.wooriyo.pinmenumobileer.util.AppHelper
import com.wooriyo.pinmenumobileer.util.AppHelper.Companion.setHeight

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
                    var strOpt = ""

                    data.opt.forEach {
                        if(strOpt == "")
                            strOpt = it
                        else
                            strOpt += "\n$it"
                    }

                    var height = 0
                    if(data.opt.size == 1)
                        height = 100
                    else if (data.opt.size <= 5)
                        height = 204
                    else if (data.opt.size <= 9)
                        height = 308
                    setHeight(layout, height)

                    option.text = strOpt
                    option.visibility = View.VISIBLE
                }else{
                    option.visibility = View.GONE
                    setHeight(layout, 100)
                }
            }
        }
    }
}