package com.wooriyo.pinmenumobileer.common.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenumobileer.databinding.ListStoreSelectBinding
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.model.StoreDTO
import com.wooriyo.pinmenumobileer.util.AppHelper

class StoreAdapter(val dataSet: ArrayList<StoreDTO>): RecyclerView.Adapter<StoreAdapter.ViewHolder>() {
    lateinit var itemClickListener: ItemClickListener

    var isFree: Boolean = false

    fun setIsFree(isFree: Boolean) {
        this.isFree = isFree
    }

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListStoreSelectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, parent.context, itemClickListener, isFree)
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

    class ViewHolder(val binding: ListStoreSelectBinding, val context: Context, val itemClickListener: ItemClickListener, val isFree: Boolean): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StoreDTO) {
            binding.run {
                storeName.text = data.name

                if(!isFree && data.paytype != 4)
                    storeName.isEnabled = data.payuse == "Y" && AppHelper.dateNowCompare(data.endDate)

                storeName.setOnClickListener {
                    itemClickListener.onItemClick(adapterPosition)
                }
            }
        }
    }
}