package com.wooriyo.pinmenumobileer.more.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenumobileer.databinding.ListQrTableBinding
import com.wooriyo.pinmenumobileer.model.PgTableDTO

class PgTableAdapter(val dataSet: ArrayList<PgTableDTO>): RecyclerView.Adapter<PgTableAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding  = ListQrTableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun checkAll(blAll: Boolean) {
        if(blAll) {
            dataSet.forEach { it.buse = "N" }
            notifyDataSetChanged()
        }
    }

    class ViewHolder(val binding: ListQrTableBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: PgTableDTO) {
            binding.run {
                tableNo.text = data.tableNo
                use.isChecked = data.buse == "Y"

                layout.setOnClickListener {
                    use.isChecked = !use.isChecked
                }

                use.setOnCheckedChangeListener { _, isChecked ->
                    if(isChecked) data.buse = "Y" else data.buse = "N"
                }
            }
        }
    }
}