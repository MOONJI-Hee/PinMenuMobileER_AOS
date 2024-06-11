package com.wooriyo.pinmenumobileer.history.adapter

import android.graphics.Color
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

                if(data.ispos == 2) {
                    noPos.visibility = View.VISIBLE
                    layout.setBackgroundColor(Color.parseColor("#F2F2F2"))
                }else {
                    noPos.visibility = View.GONE
                    layout.setBackgroundColor(Color.parseColor("#FFFFFF"))
                }

                if(data.opt.isNullOrEmpty()) {
                    option.visibility = View.GONE
                    setHeight(layout, 100)
                }else {
                    var strOpt = ""

                    data.opt.forEach {
                        if(strOpt == "")
                            strOpt = it
                        else
                            strOpt += "\n$it"
                    }

                    option.text = strOpt

                    var height = 100
                    val cnt = data.opt.size

                    val multiple = if(cnt <= 1) 0 else ((cnt - 2) / 4) + 1

                    height += (104 * multiple)

                    setHeight(layout, height)

                    option.visibility = View.VISIBLE

                }
            }
        }
    }
}