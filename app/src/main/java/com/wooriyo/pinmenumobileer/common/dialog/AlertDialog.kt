package com.wooriyo.pinmenumobileer.common.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wooriyo.pinmenumobileer.BaseDialogFragment
import com.wooriyo.pinmenumobileer.databinding.DialogAlertBinding

class AlertDialog(val title: String, val content: String): BaseDialogFragment() {
    lateinit var binding: DialogAlertBinding
    val TAG = "AlertDialog"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogAlertBinding.inflate(layoutInflater)

        if(title.isNotEmpty()) {
            binding.title.text = title
            binding.title.visibility = View.VISIBLE
        }

        binding.content.text = content

        binding.confirm.setOnClickListener { dismiss() }

        return binding.root
    }
}