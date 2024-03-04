package com.wooriyo.pinmenumobileer.menu.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.config.AppProperties
import com.wooriyo.pinmenumobileer.databinding.ListOptAddBinding
import com.wooriyo.pinmenumobileer.databinding.ListOptEditBinding
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.model.OptionDTO

class OptAdapter(val dataSet: ArrayList<OptionDTO>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var plusClickListener: View.OnClickListener
    lateinit var deleteClickListener: ItemClickListener

    fun setOnPlusClickListener(plusClickListener: OnClickListener) {
        this.plusClickListener = plusClickListener
    }

    fun setOnDeleteClickListener(deleteClickListener: ItemClickListener) {
        this.deleteClickListener = deleteClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ListOptEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val bindingAdd = ListOptAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return if(viewType == AppProperties.VIEW_TYPE_COM) ViewHolder(binding, parent.context, deleteClickListener)
                else ViewHolderAdd(bindingAdd, parent.context, plusClickListener)
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

    class ViewHolder(val binding: ListOptEditBinding, val context: Context, val deleteClickListener: ItemClickListener): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: OptionDTO) {
            binding.num.text = String.format(context.getString(R.string.opt_num), adapterPosition)

            binding.value.setText(                                                                                                                                                                                    )

            binding.delete.setOnClickListener {
                deleteClickListener.onItemClick(adapterPosition)
            }
        }
    }

    class ViewHolderAdd(val binding: ListOptAddBinding, val context: Context, val plusClickListener: OnClickListener): RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.num.text = String.format(context.getString(R.string.opt_num), adapterPosition)

            binding.btnPlus.setOnClickListener {
                plusClickListener.onClick(it)
            }
        }
    }
}