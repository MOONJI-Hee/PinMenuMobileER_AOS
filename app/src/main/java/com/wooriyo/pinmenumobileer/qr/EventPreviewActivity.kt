package com.wooriyo.pinmenumobileer.qr

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.wooriyo.pinmenumobileer.BaseActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.databinding.ActivityEventPreviewBinding


class EventPreviewActivity : BaseActivity() {
    lateinit var binding: ActivityEventPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imgUri =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("imgUri", Uri::class.java)
            } else {
                intent.getParcelableExtra("imgUri")
            }
        val exp = intent.getStringExtra("exp")
        val link = intent.getStringExtra("link")

        binding.exp.text = exp

        if(imgUri == null) {
            binding.img.visibility = View.GONE

            val lp = (binding.scrollView.layoutParams) as ConstraintLayout.LayoutParams
            lp.bottomMargin = (23.2 * MyApplication.density).toInt()
            binding.scrollView.layoutParams = lp
        }else {
            binding.img.visibility = View.VISIBLE
            Glide.with(mActivity)
                .load(imgUri)
                .transform(CenterCrop(), RoundedCorners((4* MyApplication.density).toInt()))
                .into(binding.img)
        }

        if(link.isNullOrEmpty()) {
            binding.link.visibility = View.GONE
        }else {
            binding.link.visibility = View.VISIBLE
        }

        binding.popup.clipToOutline = true

        binding.back.setOnClickListener { finish() }
        binding.close.setOnClickListener { finish() }
    }
}