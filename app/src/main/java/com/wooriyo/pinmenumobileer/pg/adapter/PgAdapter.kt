package com.wooriyo.pinmenumobileer.pg.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenumobileer.databinding.ListPgBinding
import com.wooriyo.pinmenumobileer.model.PgDetailDTO
import com.wooriyo.pinmenumobileer.pg.PgCancelActivity
import com.wooriyo.pinmenumobileer.util.AppHelper

class PgAdapter(val dataSet: ArrayList<PgDetailDTO>): RecyclerView.Adapter<PgAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PgAdapter.ViewHolder {
        val binding = ListPgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(parent.context, binding)
    }

    override fun onBindViewHolder(holder: PgAdapter.ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val context: Context, val binding: ListPgBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: PgDetailDTO) {
            binding.run {
                cardInfo.text = "${data.cardname} 뒷자리 ${data.cardnum}"
                regdt.text = data.regdt
                goods.text = data.name
                price.text = AppHelper.price(data.price)

                if(data.cancel == 0) {
                    cancelComplete.visibility = View.GONE
                    btnCancel.visibility = View.VISIBLE
                }else {
                    cancelComplete.visibility = View.VISIBLE
                    btnCancel.visibility = View.GONE
                }

                btnCancel.setOnClickListener {
                    val intent = Intent(context, PgCancelActivity::class.java)
                    intent.putExtra("ordcode", data.ordcode_key)
                    context.startActivity(intent)
                }
            }
        }
    }
}