package com.wooriyo.pinmenumobileer.menu.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ListOptBinding
import com.wooriyo.pinmenumobileer.menu.AddOptActivity
import com.wooriyo.pinmenumobileer.model.OptionDTO

class OptAdapter(val dataSet: ArrayList<OptionDTO>): RecyclerView.Adapter<OptAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListOptBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val binding: ListOptBinding, val context: Context): RecyclerView.ViewHolder(binding.root) {
        val strOpt = context.getString(R.string.option_choice)
        val strReq = context.getString(R.string.option_require)
        val colOpt = Color.parseColor("#FF0000")
        val colReq = Color.parseColor("#FFA701")
        fun bind(data: OptionDTO) {
            binding.name.text = data.title

            when(data.optreq) {
                0 -> {
                    binding.req.text = strOpt
                    binding.req.setTextColor(colOpt)
                }
                1 -> {
                    binding.req.text = strReq
                    binding.req.setTextColor(colReq)
                }
            }

            binding.layout.setOnClickListener {
                val intent = Intent(context, AddOptActivity::class.java)
                intent.putExtra("opt", data)
                context.startActivity(intent)
            }
        }
    }
}