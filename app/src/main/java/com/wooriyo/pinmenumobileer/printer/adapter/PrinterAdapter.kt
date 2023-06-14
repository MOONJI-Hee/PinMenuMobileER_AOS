package com.wooriyo.pinmenumobileer.printer.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.annotations.SerializedName
import com.sewoo.jpos.command.ESCPOSConst
import com.sewoo.jpos.printer.ESCPOSPrinter
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.config.AppProperties.Companion.FONT_BIG
import com.wooriyo.pinmenumobileer.config.AppProperties.Companion.FONT_SMALL
import com.wooriyo.pinmenumobileer.config.AppProperties.Companion.FONT_WIDTH
import com.wooriyo.pinmenumobileer.config.AppProperties.Companion.VIEW_TYPE_ADD
import com.wooriyo.pinmenumobileer.config.AppProperties.Companion.VIEW_TYPE_COM
import com.wooriyo.pinmenumobileer.databinding.ListPrinterBinding
import com.wooriyo.pinmenumobileer.databinding.ListPrinterPlusBinding
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.model.PrintDTO
import com.wooriyo.pinmenumobileer.printer.DetailPrinterActivity
import com.wooriyo.pinmenumobileer.printer.NewConnActivity
import java.io.IOException

class PrinterAdapter(val dataSet: ArrayList<PrintDTO>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var itemClickListener: ItemClickListener

    fun setConnClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ListPrinterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val binding_add = ListPrinterPlusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return if(viewType == VIEW_TYPE_COM) ViewHolder(binding, parent.context, itemClickListener) else ViewHolder_add(binding_add, parent.context)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)){
            VIEW_TYPE_COM -> {
                holder as ViewHolder
                holder.bind(dataSet[position])
            }
            VIEW_TYPE_ADD -> {
                holder as ViewHolder_add
                holder.bind()
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size+1
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == itemCount - 1) VIEW_TYPE_ADD else VIEW_TYPE_COM
    }

    class ViewHolder(val binding: ListPrinterBinding, val context: Context, val itemClickListener: ItemClickListener): RecyclerView.ViewHolder(binding.root) {
        val escposPrinter = ESCPOSPrinter()
        fun bind(data: PrintDTO) {
            var cmp = ""
            var img = 0
            when(data.printType) {
                1 -> {
                    cmp = "세우테크"
                    img = R.drawable.skl_ts400b
                }
                2-> {
                    cmp = "세우테크"
                    img = R.drawable.skl_te202
                }
                3 -> {
                    cmp = "GCUBE"
                    img = R.drawable.sam4s
                }
            }

            binding.ivPrinter.setImageResource(img)

            binding.model.text = "$cmp ${data.model}"
            if(data.nick != "") {
                binding.nick.text = data.nick
            }

            //TODO 연결된 프린터 목록과 비교해서 연결 상태 표시

            binding.btnTest.setOnClickListener {
                var rtn = 0

                try {
                    rtn = escposPrinter.printerSts()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                try {
                    escposPrinter.printAndroidFont(context.getString(R.string.print_test), FONT_WIDTH, FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
                    escposPrinter.printAndroidFont(context.getString(R.string.print_test), FONT_WIDTH, FONT_BIG, ESCPOSConst.LK_ALIGNMENT_LEFT)
                    escposPrinter.lineFeed(4)
                    escposPrinter.cutPaper()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            binding.nick.setOnClickListener {
                val intent = Intent(context, DetailPrinterActivity::class.java)
                intent.putExtra("printer", data)
                context.startActivity(intent)
            }

            binding.btnConn.setOnClickListener {
                itemClickListener.onItemClick(adapterPosition)
            }
        }
    }

    class ViewHolder_add(val binding: ListPrinterPlusBinding, val context: Context): RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.plus.setOnClickListener {
                context.startActivity(Intent(context, NewConnActivity::class.java))
            }
        }
    }
}