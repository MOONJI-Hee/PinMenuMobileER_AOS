package com.wooriyo.pinmenumobileer.common

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.MyApplication.Companion.storeList
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.common.dialog.AlertDialog
import com.wooriyo.pinmenumobileer.databinding.ActivitySelectStoreBinding
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.more.SetCustomerInfoActivity
import com.wooriyo.pinmenumobileer.printer.adapter.StoreAdapter

class SelectStoreActivity : BaseActivity() {
    lateinit var binding: ActivitySelectStoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val type = intent.getStringExtra("type")

        val storeAdapter = StoreAdapter(storeList)
        storeAdapter.setOnItemClickListener(object : ItemClickListener{
            override fun onItemClick(position: Int) {
                super.onItemClick(position)
                if(storeList[position].paytype == 2) {
                    MyApplication.store = storeList[position]
                    MyApplication.storeidx = storeList[position].idx
                    startActivity(Intent(mActivity, SetCustomerInfoActivity::class.java))
                }else {
                    AlertDialog("", getString(R.string.dialog_no_business), 1).show(supportFragmentManager, "NoBusinessDialog")
                }
            }
        })

        binding.rvStore.layoutManager = LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false)
        binding.rvStore.adapter = storeAdapter

        binding.back.setOnClickListener { finish() }
    }
}