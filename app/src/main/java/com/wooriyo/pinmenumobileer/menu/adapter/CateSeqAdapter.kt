package com.wooriyo.pinmenumobileer.menu.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ListSetCateBinding
import com.wooriyo.pinmenumobileer.listener.ItemMoveListener
import com.wooriyo.pinmenumobileer.model.CategoryDTO
import com.wooriyo.pinmenumobileer.util.TouchHelperCallback

class CateSeqAdapter(val dataSet: ArrayList<CategoryDTO>): RecyclerView.Adapter<CateSeqAdapter.ViewHolder>(), ItemMoveListener{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListSetCateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.icon.setImageResource(R.drawable.btn_category_list_move)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {

    }

    class ViewHolder(val binding: ListSetCateBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CategoryDTO) {
            binding.name.text = data.name
            binding.sub.text = data.subname
        }
    }
}