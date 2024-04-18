package com.wooriyo.pinmenumobileer.menu.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenumobileer.databinding.ListStoreSelectBinding
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.model.StoreDTO
import com.wooriyo.pinmenumobileer.util.AppHelper

class MenuStoreAdapter(val dataSet: ArrayList<StoreDTO>): RecyclerView.Adapter<MenuStoreAdapter.ViewHolder>() {
    lateinit var itemClickListener: ItemClickListener

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListStoreSelectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, parent.context, itemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemId(position: Int): Long {
        return dataSet[position].idx.toLong()
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val binding: ListStoreSelectBinding, val context: Context, val itemClickListener: ItemClickListener): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StoreDTO) {
            binding.run {
                storeName.text = data.name

                val usePay = data.payuse == "Y" && AppHelper.dateNowCompare(data.endDate)

                if(usePay) {
                    storeName.setTextColor(Color.BLACK)
                }else {
                    storeName.setTextColor(Color.parseColor("#33000000"))
                }

                storeName.setOnClickListener {
                    if(usePay || data.paytype == 4){
                        itemClickListener.onItemClick(adapterPosition)
                    }else {
                        //TODO 팝업 띄우기
                    }
                }
            }
        }
    }
}