package com.wooriyo.pinmenumobileer.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenumobileer.databinding.ListTablenoBinding
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.model.TableNoDTO

class TableNoAdapter(val dataSet: ArrayList<TableNoDTO>): RecyclerView.Adapter<TableNoAdapter.ViewHolder>(){
    lateinit var itemClickListener: ItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListTablenoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, itemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    class ViewHolder(val binding: ListTablenoBinding, val itemClickListener: ItemClickListener): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: TableNoDTO) {
            binding.tableNo.text = data.no

            binding.tableNo.isChecked = data.isChecked

            binding.tableNo.setOnClickListener {
                itemClickListener.onItemClick(adapterPosition)
            }
        }
    }
}