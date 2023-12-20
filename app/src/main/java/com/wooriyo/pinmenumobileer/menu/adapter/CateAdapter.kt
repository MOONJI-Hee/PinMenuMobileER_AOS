package com.wooriyo.pinmenumobileer.menu.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenumobileer.config.AppProperties
import com.wooriyo.pinmenumobileer.databinding.ListCateSetBinding
import com.wooriyo.pinmenumobileer.databinding.ListCateAddBinding
import com.wooriyo.pinmenumobileer.menu.AddCategoryActivity
import com.wooriyo.pinmenumobileer.menu.SetGoodsActivity
import com.wooriyo.pinmenumobileer.model.CategoryDTO

class CateAdapter(val dataSet: ArrayList<CategoryDTO>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ListCateSetBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val bindingAdd = ListCateAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return if(viewType == AppProperties.VIEW_TYPE_COM) ViewHolder(binding, parent.context) else ViewHolderAdd(bindingAdd, parent.context)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)){
            AppProperties.VIEW_TYPE_COM -> {
                holder as ViewHolder
                holder.bind(dataSet[position])
            }
            AppProperties.VIEW_TYPE_ADD -> {
                holder as ViewHolderAdd
                holder.bind()
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == dataSet.size) AppProperties.VIEW_TYPE_ADD else AppProperties.VIEW_TYPE_COM
    }

    class ViewHolder(val binding: ListCateSetBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CategoryDTO) {
            binding.name.text = data.name
            binding.sub.text = data.subname

            if(data.buse == "N") {
                binding.name.isEnabled = false
                binding.sub.isEnabled = false
            }else {
                binding.name.isEnabled = true
                binding.sub.isEnabled = true
            }

            if(adapterPosition == 0) {
                binding.margin.visibility = View.VISIBLE
            }else
                binding.margin.visibility = View.GONE

            binding.icon.setOnClickListener {
                val intent = Intent(context, AddCategoryActivity::class.java)
                intent.putExtra("type", 2)
                intent.putExtra("category", data)
                context.startActivity(intent)
            }

            binding.layout.setOnClickListener {
                val intent = Intent(context, SetGoodsActivity::class.java)
                intent.putExtra("cateidx", data.idx)
                intent.putExtra("catename", data.name)
                intent.putExtra("catecode", data.code)
                context.startActivity(intent)
            }
        }
    }

    class ViewHolderAdd(val binding: ListCateAddBinding, val context: Context): RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            if(adapterPosition == 0) {
                binding.margin.visibility = View.VISIBLE
            }else
                binding.margin.visibility = View.GONE

            binding.add.setOnClickListener {
                val intent = Intent(context, AddCategoryActivity::class.java)
                intent.putExtra("type", 1)
                context.startActivity(intent)
            }
        }
    }
}