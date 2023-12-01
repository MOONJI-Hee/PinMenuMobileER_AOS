package com.wooriyo.pinmenumobileer.common.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wooriyo.pinmenumobileer.BaseDialogFragment
import com.wooriyo.pinmenumobileer.databinding.DialogConfirmBinding

class ConfirmDialog(val title: String, val content: String, val btn: String, val onClickListener: View.OnClickListener): BaseDialogFragment() {
    lateinit var binding: DialogConfirmBinding
    val TAG = "ConfirmDialog"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogConfirmBinding.inflate(layoutInflater)

        if(title.isNotEmpty()) {
            binding.title.text = title
            binding.title.visibility = View.VISIBLE
        }

        binding.content.text = content
        binding.confirm.text = btn

        binding.cancel.setOnClickListener { dismiss() }
        binding.confirm.setOnClickListener(onClickListener)

        return binding.root
    }
}