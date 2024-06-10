package com.wooriyo.pinmenumobileer.history.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.common.dialog.AlertDialog
import com.wooriyo.pinmenumobileer.config.AppProperties
import com.wooriyo.pinmenumobileer.databinding.ListCallBinding
import com.wooriyo.pinmenumobileer.databinding.ListOrderBinding
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.order.adapter.OrderDetailAdapter
import com.wooriyo.pinmenumobileer.util.AppHelper
import com.wooriyo.pinmenumobileer.model.OrderHistoryDTO
import com.wooriyo.pinmenumobileer.order.adapter.OrderAdapter

class HistoryAdapter(val dataSet: ArrayList<OrderHistoryDTO>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var orderCompleteListener: ItemClickListener
    lateinit var callCompleteListener: ItemClickListener
    lateinit var deleteListener: ItemClickListener
    lateinit var callDeleteListener: ItemClickListener
    lateinit var printClickListener: ItemClickListener

    fun setOnOrderCompleteListener(orderCompleteListener: ItemClickListener) {
        this.orderCompleteListener = orderCompleteListener
    }

    fun setOnDeleteListener(deleteListener: ItemClickListener) {
        this.deleteListener = deleteListener
    }

    fun setOnPrintClickListener(printClickListener: ItemClickListener) {
        this.printClickListener = printClickListener
    }

    fun setOnCallCompleteListener(callCompleteListener: ItemClickListener) {
        this.callCompleteListener = callCompleteListener
    }

    fun setOnCallDeleteListener(callDeleteListener: ItemClickListener) {
        this.callDeleteListener = callDeleteListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val bindingOrder = ListOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val bindingCall = ListCallBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        bindingOrder.rv.layoutManager = LinearLayoutManager(parent.context, LinearLayoutManager.VERTICAL, false)
        bindingCall.rv.layoutManager = LinearLayoutManager(parent.context, LinearLayoutManager.VERTICAL, false)

        return if(viewType == AppProperties.VIEW_TYPE_ORDER) {
//            ViewHolderOrder(parent.context, bindingOrder, orderCompleteListener, deleteListener, printClickListener)
            OrderAdapter.ViewHolder(bindingOrder, parent.context, orderCompleteListener, deleteListener, printClickListener)
        }
        else
            ViewHolderCall(bindingCall, callCompleteListener, callDeleteListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)) {
            AppProperties.VIEW_TYPE_ORDER -> {
                holder as OrderAdapter.ViewHolder
                holder.bind(dataSet[position])
            }
            AppProperties.VIEW_TYPE_CALL -> {
                holder as ViewHolderCall
                holder.bind(dataSet[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun getItemViewType(position: Int): Int {
        return if(dataSet[position].ordType == 1) AppProperties.VIEW_TYPE_ORDER else AppProperties.VIEW_TYPE_CALL
    }

    class ViewHolderCall(val binding: ListCallBinding, val callCompleteListener: ItemClickListener, val callDeleteListener: ItemClickListener): RecyclerView.ViewHolder(binding.root) {
        fun bind (data: OrderHistoryDTO) {
            binding.run {
                rv.adapter = HisCallAdapter(data.olist)

                tableNo.text = data.tableNo
                regdt.text = data.regdt

                if(data.iscompleted == 1) {
                    top.setBackgroundColor(Color.parseColor("#E0E0E0"))
                    done.visibility = View.VISIBLE
                    complete.isEnabled = false
                }else {
                    top.setBackgroundResource(R.color.main)
                    done.visibility = View.GONE
                    complete.isEnabled = true
                }

                delete.setOnClickListener { callDeleteListener.onItemClick(adapterPosition) }
                complete.setOnClickListener { callCompleteListener.onItemClick(adapterPosition) }
            }
        }
    }
}