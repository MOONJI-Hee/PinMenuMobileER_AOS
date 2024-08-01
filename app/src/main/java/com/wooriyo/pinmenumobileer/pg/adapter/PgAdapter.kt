package com.wooriyo.pinmenumobileer.pg.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenumobileer.R
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
                if(data.cardname == "KB국민")
                    cardInfo.text = data.cardname
                else
                    cardInfo.text = "${data.cardname} 뒷자리 ${data.cardnum}"
                regdt.text = data.regdt
                goods.text = data.name
                price.text = AppHelper.price(data.price)

                if(data.cancel == 0) {
                    cancelComplete.visibility = View.GONE
                    btnCancel.setTextColor(Color.BLACK)
                    btnCancel.setBackgroundResource(R.drawable.bg_r6_grd)
                    btnCancel.text = context.getString(R.string.payment_cancel)
                }else {
                    cancelComplete.visibility = View.VISIBLE
                    btnCancel.setTextColor(Color.RED)
                    btnCancel.setBackgroundResource(R.drawable.bg_r6g)
                    btnCancel.text = context.getString(R.string.payment_cancel_complete)
                }

                btnCancel.setOnClickListener {
                    val intent = Intent(context, PgCancelActivity::class.java)
                    intent.putExtra("ordcode", data.ordcode_key)
                    intent.putExtra("tid", data.tid)
                    context.startActivity(intent)
                }
            }
        }
    }
}