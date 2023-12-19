package com.wooriyo.pinmenumobileer.menu

import android.os.Build
import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.databinding.ActivityChangeSeqBinding
import com.wooriyo.pinmenumobileer.menu.adapter.CateAdapter
import com.wooriyo.pinmenumobileer.menu.adapter.CateSeqAdapter
import com.wooriyo.pinmenumobileer.model.CategoryDTO
import com.wooriyo.pinmenumobileer.util.TouchHelperCallback

class ChangeSeqActivity : BaseActivity() {
    lateinit var binding: ActivityChangeSeqBinding

    lateinit var cateList: ArrayList<CategoryDTO>
    lateinit var cateAdapter: CateSeqAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeSeqBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cateList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getSerializableExtra("cateList", CategoryDTO::class.java) as ArrayList<CategoryDTO>
        else
            intent.getSerializableExtra("cateList") as ArrayList<CategoryDTO>

        cateAdapter = CateSeqAdapter(cateList)
        val touchHelperCallback = TouchHelperCallback(cateAdapter)
        val touchHelper = ItemTouchHelper(touchHelperCallback)
        touchHelper.attachToRecyclerView(binding.rvCate)

        binding.rvCate.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        binding.rvCate.adapter = cateAdapter

        binding.back.setOnClickListener { finish() }
    }
}