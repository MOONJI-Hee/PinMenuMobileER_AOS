package com.wooriyo.pinmenumobileer.menu.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenumobileer.databinding.ListOptAddBinding
import com.wooriyo.pinmenumobileer.databinding.ListOptEditBinding
import com.wooriyo.pinmenumobileer.model.OptionDTO

class OptAdapter(val dataSet: ArrayList<OptionDTO>): RecyclerView.Adapter<OptAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return dataSet.size + 1
    }

    class ViewHolder(val binding: ListOptEditBinding): RecyclerView.ViewHolder(binding.root) {

    }

    class ViewHolderAdd(val binding: ListOptAddBinding): RecyclerView.ViewHolder(binding.root) {

    }
}