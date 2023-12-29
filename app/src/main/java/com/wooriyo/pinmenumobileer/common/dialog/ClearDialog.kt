package com.wooriyo.pinmenumobileer.common.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.wooriyo.pinmenumobileer.BaseDialogFragment
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.DialogClearBinding

class ClearDialog(val callClickListener: OnClickListener, val ordClickListener: OnClickListener): BaseDialogFragment() {
    lateinit var binding: DialogClearBinding
    val TAG = "ConfirmDialog"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogClearBinding.inflate(layoutInflater)

        binding.close.setOnClickListener { dismiss() }

        binding.clearCall.setOnClickListener {
            callClickListener.onClick(it)
            dismiss()
        }

        binding.clearOrder.setOnClickListener {
            ordClickListener.onClick(it)
            dismiss()
        }

        return binding.root
    }
}