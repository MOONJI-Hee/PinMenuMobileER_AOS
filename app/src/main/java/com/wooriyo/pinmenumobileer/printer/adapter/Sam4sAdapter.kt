package com.wooriyo.pinmenumobileer.printer.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.sam4s.io.ethernet.SocketInfo
import com.sam4s.printer.Sam4sBuilder
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.common.dialog.AlertDialog
import com.wooriyo.pinmenumobileer.databinding.ListPrinterBinding
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.printer.DetailPrinterActivity
import com.wooriyo.pinmenumobileer.util.AppHelper

class Sam4sAdapter(val dataSet: ArrayList<SocketInfo>): RecyclerView.Adapter<Sam4sAdapter.ViewHolder>() {
    lateinit var itemClickListener: ItemClickListener

    fun setConnClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListPrinterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, parent.context, itemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val binding: ListPrinterBinding, val context: Context, val itemClickListener: ItemClickListener): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: SocketInfo) {
            val img = R.drawable.sam4s
            binding.ivPrinter.setImageResource(img)
            binding.model.text = "Sam4S GCUBE"

            // 연결 상태에 따라 우측 버튼 및 뷰 변경
            if(AppHelper.checkCubeConn(context) == 1) {
                binding.btnConn.visibility = View.INVISIBLE
                binding.connNo.visibility = View.INVISIBLE
                binding.btnClear.visibility = View.VISIBLE
                binding.connDot.visibility = View.VISIBLE
                binding.connStatus.visibility = View.VISIBLE
                binding.connStatus.text = context.getString(R.string.good)

                binding.btnTest.setOnClickListener {
                    MyApplication.cubeBuilder.addText(context.getString(R.string.print_test))
                    MyApplication.cubeBuilder.addFeedLine(4)
                    MyApplication.cubeBuilder.addCut(Sam4sBuilder.CUT_NO_FEED)
                    try {
                        MyApplication.INSTANCE.mPrinterConnection?.sendData(MyApplication.cubeBuilder)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }else {
                binding.btnConn.visibility = View.VISIBLE
                binding.connNo.visibility = View.VISIBLE
                binding.btnClear.visibility = View.GONE
                binding.connDot.visibility = View.GONE
                binding.connStatus.visibility = View.GONE

                binding.btnTest.setOnClickListener {
                    val fragmentActivity = context as FragmentActivity
                    AlertDialog("", context.getString(R.string.dialog_check_conn), 1).show(fragmentActivity.supportFragmentManager, "AlertDialog")
                }
            }

            binding.layout.setOnClickListener {
                val intent = Intent(context, DetailPrinterActivity::class.java)
                intent.putExtra("sam4s", data)
                context.startActivity(intent)
            }

            binding.btnTest.setOnClickListener {
                MyApplication.cubeBuilder.createCommandBuffer()
                MyApplication.cubeBuilder.addText(context.getString(R.string.print_test))
                MyApplication.cubeBuilder.addFeedLine(4)
                MyApplication.cubeBuilder.addCut(Sam4sBuilder.CUT_NO_FEED)
                try {
                    MyApplication.INSTANCE.mPrinterConnection?.sendData(MyApplication.cubeBuilder)
                    MyApplication.cubeBuilder.clearCommandBuffer()
                } catch (e: Exception) {
                    e.printStackTrace()
                    val fragmentActivity = context as FragmentActivity
                    AlertDialog("", context.getString(R.string.dialog_check_conn), 1).show(fragmentActivity.supportFragmentManager, "AlertDialog")
                }

//                if(AppHelper.checkCubeConn(context) == 1) {
//                    MyApplication.cubeBuilder.createCommandBuffer()
//                    MyApplication.cubeBuilder.addText(context.getString(R.string.print_test))
//                    MyApplication.cubeBuilder.addFeedLine(4)
//                    MyApplication.cubeBuilder.addCut(Sam4sBuilder.CUT_NO_FEED)
//                    try {
//                        MyApplication.INSTANCE.mPrinterConnection?.sendData(MyApplication.cubeBuilder)
//                        MyApplication.cubeBuilder.clearCommandBuffer()
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }else {
//                    val fragmentActivity = context as FragmentActivity
//                    AlertDialog("", context.getString(R.string.dialog_check_conn), 1).show(fragmentActivity.supportFragmentManager, "AlertDialog")
//                }
            }

            binding.btnConn.setOnClickListener {
                itemClickListener.onItemClick(adapterPosition)
            }
        }
    }
}