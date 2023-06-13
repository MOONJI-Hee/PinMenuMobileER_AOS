package com.wooriyo.pinmenumobileer.printer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.databinding.ActivitySelectStoreBinding
import com.wooriyo.pinmenumobileer.model.StoreDTO
import com.wooriyo.pinmenumobileer.printer.adapter.StoreAdapter

class SelectStoreActivity : BaseActivity() {
    lateinit var binding: ActivitySelectStoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storeList = intent.getSerializableExtra("storeList") as ArrayList<StoreDTO>

        binding.rvStore.layoutManager = LinearLayoutManager(this@SelectStoreActivity, RecyclerView.VERTICAL, false)
        binding.rvStore.adapter = StoreAdapter(storeList)

        binding.back.setOnClickListener { finish() }
    }
}