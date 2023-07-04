package com.wooriyo.pinmenumobileer.order.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenumobileer.databinding.ListOrderDetailBinding
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.model.OrderDTO
import com.wooriyo.pinmenumobileer.util.AppHelper

class OrderPayDetailAdapter(val dataSet: ArrayList<OrderDTO>): RecyclerView.Adapter<OrderPayDetailAdapter.ViewHolder>() {
    lateinit var onItemClickListener: ItemClickListener

    fun setOnCheckListener(onItemClickListener: ItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListOrderDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onItemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val binding: ListOrderDetailBinding, val onItemClickListener: ItemClickListener): RecyclerView.ViewHolder(binding.root) {
        fun bind (data : OrderDTO) {
            binding.run {
                name.text = data.name
                gea.text = data.gea.toString()
                price.text = AppHelper.price(data.price)

                if(data.opt.isNotEmpty()) {
                    option.text = data.opt.replace(" / ", "\n")
                    option.visibility = View.VISIBLE
                    //TODO 옵션 개수에 따라 뷰 높이 지정
                }else{
                    option.visibility = View.GONE
                    //TODO 뷰 높이 100dp로 지정
                }

                select.visibility = View.VISIBLE
                select.isChecked = data.isChecked

                layout.setOnClickListener {
                    select.isChecked = !select.isChecked
                }

                select.setOnCheckedChangeListener { v, isChecked ->
                    onItemClickListener.onCheckClick(adapterPosition, v as CheckBox, isChecked)
                }
            }
        }
    }
}