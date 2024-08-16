package com.wooriyo.pinmenumobileer.more.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenumobileer.databinding.ListLanguageBinding
import com.wooriyo.pinmenumobileer.model.LanguageDTO

class LanguageAdapter(val dataSet: ArrayList<LanguageDTO>): RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val binding = ListLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(parent.context, binding)
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bind(dataSet[p1])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val context: Context, val binding: ListLanguageBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: LanguageDTO) {
            binding.run {
                layout.setOnClickListener {
                    check.isChecked = !check.isChecked
                    data.isChecked = check.isChecked
                }

                check.setOnCheckedChangeListener { _, isChecked ->
                    if(isChecked)
                        disable.visibility = View.GONE
                    else
                        disable.visibility = View.VISIBLE
                }

                tvLang.text = data.lang
                imgLang.setImageResource(context.resources.getIdentifier(data.img, "drawable", "com.wooriyo.us.pinmenumobileer"))
                check.isChecked = data.isChecked
            }
        }
    }
}