package com.wooriyo.pinmenumobileer.qr.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wooriyo.pinmenumobileer.MainActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.common.dialog.AlertDialog
import com.wooriyo.pinmenumobileer.databinding.ListQrBinding
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.model.QrDTO
import com.wooriyo.pinmenumobileer.qr.QrDetailActivity
import com.wooriyo.pinmenumobileer.util.AppHelper

class QrAdapter(val dataSet: ArrayList<QrDTO>): RecyclerView.Adapter<QrAdapter.ViewHolder>() {
    lateinit var postPayClickListener: ItemClickListener

    var qrCnt = 0

    fun setOnPostPayClickListener(postPayClickListener: ItemClickListener) {
        this.postPayClickListener = postPayClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListQrBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position], qrCnt, postPayClickListener)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun setQrCount(qrCnt: Int) {
        this.qrCnt = qrCnt
    }

    class ViewHolder(val binding: ListQrBinding, val context: Context):RecyclerView.ViewHolder(binding.root) {
        fun bind(data: QrDTO, qrCnt: Int, postPayClickListener: ItemClickListener) {
            binding.able.visibility = View.VISIBLE
            binding.plus.visibility = View.GONE

            binding.tableNo.text = data.tableNo
            binding.seq.text = String.format(context.getString( R.string.qr_cnt), AppHelper.intToString(adapterPosition+1))

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

            binding.disable.setOnClickListener {
                AlertDialog("", context.getString(R.string.dialog_disable_qr)).show((context as MainActivity).supportFragmentManager, "DisableQrDialog")
            }

            binding.postPay.setOnClickListener {
                it as CheckBox
                if(MyApplication.store.paytype == 2) {
                    postPayClickListener.onQrClick(adapterPosition, it.isChecked)
                }else {
                    it.isChecked = false
                    AlertDialog("", context.getString(R.string.dialog_no_business)).show((context as MainActivity).supportFragmentManager, "NoBusinessDialog")
                }
            }
        }
    }
}