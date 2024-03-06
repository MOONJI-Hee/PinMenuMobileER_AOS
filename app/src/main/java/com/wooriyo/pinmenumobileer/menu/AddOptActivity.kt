package com.wooriyo.pinmenumobileer.menu

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.config.AppProperties
import com.wooriyo.pinmenumobileer.config.AppProperties.Companion.RESULT_MODIFY
import com.wooriyo.pinmenumobileer.databinding.ActivityAddOptBinding
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.menu.adapter.OptValAdapter
import com.wooriyo.pinmenumobileer.model.OptionDTO
import com.wooriyo.pinmenumobileer.model.ValueDTO

class AddOptActivity : BaseActivity() {
    lateinit var binding: ActivityAddOptBinding
    lateinit var option: OptionDTO

    lateinit var valueAdapter: OptValAdapter

    var type = 0        // 0: 추가, 1: 수정
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddOptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        type = intent.getIntExtra("type", 0)

        if(type == 1) {
            binding.add.visibility = View.GONE
            binding.delete.visibility = View.VISIBLE
            binding.modify.visibility = View.VISIBLE

            binding.optName.setText(option.title)
        }

        option = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("opt", OptionDTO::class.java) as OptionDTO
        }else {
            intent.getSerializableExtra("opt") as OptionDTO
        }

        if(option.optreq == 0) {
            binding.title.text = resources.getString(R.string.option_choice)
            binding.tv1.text = resources.getString(R.string.opt_chc_name)
        }else {
            binding.title.text = resources.getString(R.string.option_require)
            binding.tv1.text = resources.getString(R.string.opt_req_name)
        }

        valueAdapter = OptValAdapter(option.optval)
        valueAdapter.setOnPlusClickListener(View.OnClickListener {
            option.optval.add(ValueDTO())
            valueAdapter.notifyItemChanged(option.optval.size -1)
            valueAdapter.notifyItemInserted(option.optval.size)
        })
        valueAdapter.setOnDeleteClickListener(object : ItemClickListener{
            override fun onItemClick(position: Int) {
                option.optval.removeAt(position)
                valueAdapter.notifyItemRemoved(position)
                valueAdapter.notifyItemRangeChanged(position, option.optval.size - position)
            }
        })

        binding.rv.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        binding.rv.adapter = valueAdapter

        binding.back.setOnClickListener { finish() }
        binding.delete.setOnClickListener {
            setResult(AppProperties.RESULT_DELETE)
            finish()
        }
        binding.modify.setOnClickListener { save() }
        binding.add.setOnClickListener { save() }
    }

    fun save() {
        val title = binding.optName.text.toString()
        if(title.isEmpty()) {
            Toast.makeText(mActivity, R.string.opt_hint, Toast.LENGTH_SHORT).show()
            return
        }
        option.title = title

        intent.putExtra("result_opt", option)
        if(type == 0) setResult(RESULT_OK) else setResult(RESULT_MODIFY)
        finish()
    }
}