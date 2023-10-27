package com.wooriyo.pinmenumobileer.printer.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.sam4s.io.ethernet.SocketInfo
import com.sewoo.jpos.command.ESCPOSConst
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.common.AlertDialog
import com.wooriyo.pinmenumobileer.config.AppProperties
import com.wooriyo.pinmenumobileer.databinding.ListPrinterBinding
import com.wooriyo.pinmenumobileer.printer.DetailPrinterActivity
import java.io.IOException

class Sam4sAdapter(val dataSet: ArrayList<SocketInfo>): RecyclerView.Adapter<Sam4sAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListPrinterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val binding: ListPrinterBinding, val context: Context): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: SocketInfo) {
            val img = R.drawable.sam4s
            binding.ivPrinter.setImageResource(img)
            binding.model.text = "Sam4S GCUBE"

            // 연결 상태에 따라 우측 버튼 및 뷰 변경

//                if(data.connected) {
//                binding.btnConn.visibility = View.INVISIBLE
//                binding.connNo.visibility = View.INVISIBLE
//                binding.btnClear.visibility = View.VISIBLE
//                binding.connDot.visibility = View.VISIBLE
//                binding.connStatus.visibility = View.VISIBLE
//                binding.connStatus.text = context.getString(R.string.good)
//            }else {
//                binding.btnConn.visibility = View.VISIBLE
//                binding.connNo.visibility = View.VISIBLE
//                binding.btnClear.visibility = View.GONE
//                binding.connDot.visibility = View.GONE
//                binding.connStatus.visibility = View.GONE
//            }

            binding.layout.setOnClickListener {
                val intent = Intent(context, DetailPrinterActivity::class.java)
                intent.putExtra("printer_cube", data)
                context.startActivity(intent)
            }

            binding.btnTest.setOnClickListener {
                if(MyApplication.bluetoothPort.isConnected) {
                    try {
                        MyApplication.escposPrinter.printAndroidFont(context.getString(R.string.print_test),
                            AppProperties.FONT_WIDTH,
                            AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
                        MyApplication.escposPrinter.printAndroidFont(context.getString(R.string.print_test),
                            AppProperties.FONT_WIDTH,
                            AppProperties.FONT_BIG, ESCPOSConst.LK_ALIGNMENT_LEFT)
                        MyApplication.escposPrinter.lineFeed(4)
                        MyApplication.escposPrinter.cutPaper()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }else {
                    val fragmentActivity = context as FragmentActivity
                    AlertDialog("", context.getString(R.string.dialog_check_conn), 1).show(fragmentActivity.supportFragmentManager, "AlertDialog")
                }
            }
//            binding.btnConn.setOnClickListener {
//                itemClickListener.onItemClick(adapterPosition)
//            }
        }
    }
}