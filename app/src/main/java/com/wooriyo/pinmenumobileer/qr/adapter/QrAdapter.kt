package com.wooriyo.pinmenumobileer.qr.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.ListQrBinding
import com.wooriyo.pinmenumobileer.model.QrDTO
import com.wooriyo.pinmenumobileer.qr.QrDetailActivity

class QrAdapter(val dataSet: ArrayList<QrDTO>): RecyclerView.Adapter<QrAdapter.ViewHolder>() {
    var qrCnt = 0
    var storeName = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListQrBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet, qrCnt)
    }

    override fun getItemCount(): Int {
        return if(qrCnt < dataSet.size) dataSet.size else qrCnt
    }

    fun setQrCount(qrCnt: Int) {
        this.qrCnt = qrCnt
    }

    class ViewHolder(val binding: ListQrBinding, val context: Context):RecyclerView.ViewHolder(binding.root) {
        fun bind(dataSet: ArrayList<QrDTO>, qrCnt: Int) {
            val intent = Intent(context, QrDetailActivity::class.java)

            binding.seq.text = String.format(context.getString( R.string.qr_cnt), adapterPosition+1)
            intent.putExtra("seq", adapterPosition+1)

            run task@ {
                dataSet.forEach{
                    if(adapterPosition+1 == it.seq) {
                        binding.able.visibility = View.VISIBLE
                        binding.plus.visibility = View.GONE

                        binding.tableNo.text = it.tableNo

                        Glide.with(context)
                            .load(it.filePath)
                            .into(binding.ivQr)

                        if(qrCnt < it.seq) {
                            binding.disable.visibility = View.VISIBLE
                        }else
                            binding.disable.visibility = View.GONE

                        intent.putExtra("qrcode", it)

                        return@task
                    }else {
                        intent.removeExtra("qrcode")
                        binding.able.visibility = View.GONE
                        binding.plus.visibility = View.VISIBLE
                    }
                }
            }

            val onDetailClick = View.OnClickListener {
                context.startActivity(intent)
            }
            binding.plus.setOnClickListener(onDetailClick)
            binding.able.setOnClickListener(onDetailClick)
        }
    }
}