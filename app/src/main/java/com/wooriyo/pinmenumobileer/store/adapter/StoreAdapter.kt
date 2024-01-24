package com.wooriyo.pinmenumobileer.store.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.call.CallListActivity
import com.wooriyo.pinmenumobileer.databinding.ListStoreBinding
import com.wooriyo.pinmenumobileer.history.ByHistoryActivity
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.menu.SetCategoryActivity
import com.wooriyo.pinmenumobileer.model.StoreDTO
import com.wooriyo.pinmenumobileer.order.OrderListActivity
import com.wooriyo.pinmenumobileer.pg.PgHistoryActivity
import com.wooriyo.pinmenumobileer.pg.dialog.NoPgInfoDialog
import com.wooriyo.pinmenumobileer.util.AppHelper
import com.wooriyo.pinmenumobileer.util.AppHelper.Companion.dateNowCompare

class StoreAdapter(val dataSet: ArrayList<StoreDTO>): RecyclerView.Adapter<StoreAdapter.ViewHolder>() {
    lateinit var itemClickListener: ItemClickListener

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    class ViewHolder(val binding: ListStoreBinding, val context: Context, val itemClickListener: ItemClickListener): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StoreDTO) {
            binding.run {
                storeName.text = data.name
                ordCnt.text = data.ordCnt.toString()
                menuCnt.text = data.menuCnt.toString()
                payCnt.text = data.pgCnt.toString()

                btnOrder.setOnClickListener{
                    ordCnt.isPressed = true
                    ordTxt.isPressed = true
                    itemClickListener.onStoreClick(data, Intent(context, ByHistoryActivity::class.java))
                }
//                btnCall.setOnClickListener {
//                    callCnt.isPressed = true
//                    callTxt.isPressed = true
//                    itemClickListener.onStoreClick(data, Intent(context, CallListActivity::class.java))
//                }

                btnMenu.setOnClickListener {
                    menuCnt.isPressed = true
                    menuTxt.isPressed = true

                    if((data.payuse == "Y" && dateNowCompare(data.paydate)) || adapterPosition == 0) {
                        MyApplication.store = MyApplication.storeList[adapterPosition]
                        MyApplication.storeidx = MyApplication.storeList[adapterPosition].idx

                        context.startActivity(Intent(context, SetCategoryActivity::class.java))
                    }
                }

                btnPayHistory.setOnClickListener {
                    if(data.pg_storenm.isEmpty() || data.pg_snum.isEmpty()) {
                        NoPgInfoDialog().show((context as FragmentActivity).supportFragmentManager, "NoPgInfoDialog")
                    }else {
                        MyApplication.storeidx = data.idx
                        context.startActivity(Intent(context, PgHistoryActivity::class.java))
                    }
                }

                if(data.pg_storenm.isEmpty() || data.pg_snum.isEmpty()) {
                    payCnt.isEnabled = false
                    payTxt.isEnabled = false
                }else {
                    payCnt.isEnabled = true
                    payTxt.isEnabled = true
                }

                if(data.payuse == "Y" && dateNowCompare(data.paydate)) {
                    storeName.isEnabled = true
                    ordCnt.isEnabled = true
                    ordTxt.isEnabled = true
                    btnOrder.isEnabled = true
                    menuCnt.isEnabled = true
                    menuTxt.isEnabled = true
                    payCnt.isEnabled = true
                    payTxt.isEnabled = true
                    btnPayHistory.isEnabled = true
                }else {
                    storeName.isEnabled = false
                    ordCnt.isEnabled = false
                    ordTxt.isEnabled = false
                    btnOrder.isEnabled = false
                    menuCnt.isEnabled = false
                    menuTxt.isEnabled = false
                    payCnt.isEnabled = false
                    payTxt.isEnabled = false
                    btnPayHistory.isEnabled = false
                }
            }
        }
    }
}