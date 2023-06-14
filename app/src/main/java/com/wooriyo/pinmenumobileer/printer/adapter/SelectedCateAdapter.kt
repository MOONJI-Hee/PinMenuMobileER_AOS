package com.wooriyo.pinmenumobileer.printer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenumobileer.databinding.ListPrinterCateBinding

class SelectedCateAdapter(val dataSet: List<String>): RecyclerView.Adapter<SelectedCateAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListPrinterCateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val binding: ListPrinterCateBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: String) {
            binding.name.text = data
        }
    }
}