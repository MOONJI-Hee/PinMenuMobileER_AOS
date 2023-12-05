package com.wooriyo.pinmenumobileer.common.dialog

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.wooriyo.pinmenumobileer.BaseDialogFragment
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.DialogConfirmBinding

class UpdateDialog(val update: Int, val msg: String): BaseDialogFragment() {
    lateinit var binding: DialogConfirmBinding
    lateinit var onClickListener: OnClickListener
    val TAG = "UpdateDialog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DialogConfirmBinding.inflate(layoutInflater)

        if(update == 0) {   // 권장 업데이트
            binding.cancel.run {
                text = "나중에 하기"
                setOnClickListener {
                    onClickListener.onClick(it)
                    dismiss()
                }
            }
        }else if (update == 1) {
            binding.run {
                cancel.visibility = View.GONE
                confirm.setBackgroundResource(R.drawable.bg_rb6_grd)
            }
        }

        binding.content.text = msg
        binding.confirm.text = "업데이트"
        binding.confirm.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context?.packageName}")))
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context?.packageName}")))
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        dismiss()
    }

    fun setCancelClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }
}