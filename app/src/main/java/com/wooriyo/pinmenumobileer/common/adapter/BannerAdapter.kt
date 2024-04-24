package com.wooriyo.pinmenumobileer.common.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wooriyo.pinmenumobileer.databinding.ListBannerBinding
import com.wooriyo.pinmenumobileer.model.PopupDTO

class BannerAdapter(val bannerList: ArrayList<PopupDTO>) : RecyclerView.Adapter<BannerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(bannerList[position])
    }

    override fun getItemCount(): Int {
        return bannerList.size
    }

    override fun getItemId(position: Int): Long {
        return bannerList[position].idx.toLong()
    }

    class ViewHolder(val binding: ListBannerBinding, val context: Context): RecyclerView.ViewHolder(binding.root) {
        fun bind (banner: PopupDTO) {
            Glide.with(context)
                .load(banner.link)
                .into(binding.img)

            binding.img.setOnClickListener {
                context.startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(banner.link) })
            }
        }
    }

}