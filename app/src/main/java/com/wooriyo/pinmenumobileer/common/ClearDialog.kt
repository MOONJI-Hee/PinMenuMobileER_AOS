package com.wooriyo.pinmenumobileer.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.wooriyo.pinmenumobileer.BaseDialogFragment
import com.wooriyo.pinmenumobileer.databinding.DialogClearBinding

class ClearDialog(val onClickListener: OnClickListener): BaseDialogFragment() {
    lateinit var binding: DialogClearBinding
    val TAG = "ConfirmDialog"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogClearBinding.inflate(layoutInflater)

        binding.clear.setOnClickListener(onClickListener)
        binding.confirm.setOnClickListener{ dismiss() }

        return binding.root
    }
}