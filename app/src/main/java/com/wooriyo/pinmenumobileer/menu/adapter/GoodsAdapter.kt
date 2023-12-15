package com.wooriyo.pinmenumobileer.menu.adapter

import android.content.Context
import android.content.Intent
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.config.AppProperties
import com.wooriyo.pinmenumobileer.databinding.ListMenuAddBinding
import com.wooriyo.pinmenumobileer.databinding.ListMenuSetBinding
import com.wooriyo.pinmenumobileer.menu.AddGoodsActivity
import com.wooriyo.pinmenumobileer.model.GoodsDTO
import com.wooriyo.pinmenumobileer.util.AppHelper

class GoodsAdapter(val dataSet: ArrayList<GoodsDTO>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ListMenuSetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val bindingAdd = ListMenuAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return if(viewType == AppProperties.VIEW_TYPE_ADD) ViewHolderAdd(bindingAdd, parent.context) else ViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position) == AppProperties.VIEW_TYPE_COM) {
            holder as ViewHolder
            holder.bind(dataSet[position])
        }else {
            holder as ViewHolderAdd
            holder.bind()
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == dataSet.size) AppProperties.VIEW_TYPE_ADD else AppProperties.VIEW_TYPE_COM
    }

    class ViewHolder(val binding: ListMenuSetBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
        val matrix = ColorMatrix().apply { setSaturation(0f) }
        val grayfilter = ColorMatrixColorFilter(matrix)

        fun bind(data: GoodsDTO) {
            binding.run {
                name.text = data.name
                price.text = AppHelper.price(data.price)

                Glide.with(context)
                    .load(data.img1)
                    .transform(CenterCrop())
                    .into(img)

                // 메뉴 상태 > 1: None, 2: Hide, 3: Best, 4: Sold Out, 5: New
                var status = 0
                when(data.icon) {
                    3 -> status = R.drawable.img_badge_best
                    5 -> status = R.drawable.img_badge_new
                }

                if(status != 0) {
                    icon.setImageResource(status)
                    icon.visibility = View.VISIBLE
                }else {
                    icon.visibility = View.GONE
                }

                if(data.icon == 4) {
                    img.colorFilter = grayfilter
                    soldout.visibility = View.VISIBLE
                }else {
                    img.colorFilter = null
                    soldout.visibility = View.GONE
                }

                setting.setOnClickListener {
                    val intent = Intent(context, AddGoodsActivity::class.java)
                    intent.putExtra("type", 2)
                    intent.putExtra("goods", data)
                    context.startActivity(intent)
                }
            }
        }
    }

    class ViewHolderAdd(val binding: ListMenuAddBinding, val context: Context): RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.add.setOnClickListener {
                val intent = Intent(context, AddGoodsActivity::class.java)
                intent.putExtra("type", 1)
                context.startActivity(intent)
            }
        }
    }
}