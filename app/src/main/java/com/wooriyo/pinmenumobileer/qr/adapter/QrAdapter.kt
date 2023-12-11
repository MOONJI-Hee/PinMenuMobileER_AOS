package com.wooriyo.pinmenumobileer.qr.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.config.AppProperties
import com.wooriyo.pinmenumobileer.databinding.ListQrBinding
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.model.QrDTO
import com.wooriyo.pinmenumobileer.qr.QrDetailActivity

class QrAdapter(val dataSet: ArrayList<QrDTO>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var postPayClickListener: ItemClickListener

    var qrCnt = 0

    fun setOnPostPayClickListener(postPayClickListener: ItemClickListener) {
        this.postPayClickListener = postPayClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ListQrBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return if(viewType == AppProperties.VIEW_TYPE_COM) ViewHolder(binding, parent.context) else ViewHolder_add(binding, parent.context)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)){
            AppProperties.VIEW_TYPE_COM -> {
                holder as ViewHolder
                holder.bind(dataSet[position], qrCnt, postPayClickListener)
            }
            AppProperties.VIEW_TYPE_ADD -> {
                holder as ViewHolder_add
                holder.bind()
            }
        }
    }

    override fun getItemCount(): Int {
        return if(dataSet.size < qrCnt) dataSet.size + 1 else dataSet.size
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == dataSet.size) AppProperties.VIEW_TYPE_ADD else AppProperties.VIEW_TYPE_COM
    }

    fun setQrCount(qrCnt: Int) {
        this.qrCnt = qrCnt
    }

    class ViewHolder(val binding: ListQrBinding, val context: Context):RecyclerView.ViewHolder(binding.root) {
        fun bind(data: QrDTO, qrCnt: Int, postPayClickListener: ItemClickListener) {
            binding.able.visibility = View.VISIBLE
            binding.plus.visibility = View.GONE

            binding.tableNo.text = data.tableNo
            binding.seq.text = String.format(context.getString( R.string.qr_cnt), adapterPosition+1)

            Glide.with(context)
                .load(data.filePath)
                .into(binding.ivQr)

            if(qrCnt < adapterPosition+1) {
                binding.disable.visibility = View.VISIBLE
            }else
                binding.disable.visibility = View.GONE

            binding.postPay.isChecked = data.qrbuse == "Y"

            val intent = Intent(context, QrDetailActivity::class.java)
            intent.putExtra("seq", adapterPosition+1)
            intent.putExtra("qrcode", data)

            binding.able.setOnClickListener{
                context.startActivity(intent)
            }

            binding.postPay.setOnClickListener {
                postPayClickListener.onQrClick(adapterPosition, (it as CheckBox).isChecked)
            }
        }
    }

    class ViewHolder_add(val binding: ListQrBinding, val context: Context): RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.able.visibility = View.GONE
            binding.plus.visibility = View.VISIBLE

            val intent = Intent(context, QrDetailActivity::class.java)
            intent.putExtra("seq", adapterPosition+1)

            binding.plus.setOnClickListener{
                context.startActivity(intent)
            }
        }
    }
}