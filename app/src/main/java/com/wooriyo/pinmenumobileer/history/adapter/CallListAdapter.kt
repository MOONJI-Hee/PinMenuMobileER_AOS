package com.wooriyo.pinmenumobileer.history.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.history.adapter.CallListAdapter.ViewHolder
import com.wooriyo.pinmenumobileer.databinding.ListCallBinding
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.model.CallHistoryDTO

class CallListAdapter(val dataSet: ArrayList<CallHistoryDTO>): RecyclerView.Adapter<ViewHolder>() {
    lateinit var completeListener: ItemClickListener
    lateinit var deleteListener: ItemClickListener

    fun setOnItemClickListener(completeListener: ItemClickListener) {
        this.completeListener = completeListener
    }

    fun setOnDeleteListener(deleteListener: ItemClickListener) {
        this.deleteListener = deleteListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListCallBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.rv.layoutManager = LinearLayoutManager(parent.context, LinearLayoutManager.VERTICAL, false)
        return ViewHolder(binding, completeListener, deleteListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val binding: ListCallBinding, val completeListener : ItemClickListener, val deleteListener: ItemClickListener): RecyclerView.ViewHolder(binding.root) {
        fun bind (data : CallHistoryDTO) {
            binding.run {
                rv.adapter = CallDetailAdapter(data.clist)

                tableNo.text = data.tableNo
                regdt.text = data.regDt

                if(data.iscompleted == 1) {
                    top.setBackgroundColor(Color.parseColor("#E0E0E0"))
                    done.visibility = View.VISIBLE
                    complete.isEnabled = false
                }else {
                    top.setBackgroundResource(R.color.main)
                    done.visibility = View.GONE
                    complete.isEnabled = true
                }

                delete.setOnClickListener { deleteListener.onItemClick(adapterPosition) }
                complete.setOnClickListener { completeListener.onItemClick(adapterPosition) }
            }
        }
    }
}