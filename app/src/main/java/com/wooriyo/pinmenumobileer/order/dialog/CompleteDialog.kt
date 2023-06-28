package com.wooriyo.pinmenumobileer.order.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wooriyo.pinmenumobileer.BaseDialogFragment
import com.wooriyo.pinmenumobileer.databinding.DialogCompleteBinding

class CompleteDialog: BaseDialogFragment() {
    lateinit var binding: DialogCompleteBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogCompleteBinding.inflate(layoutInflater)

        binding.complete.setOnClickListener {

        }
        binding.cancel.setOnClickListener { dismiss() }
        return binding.root
    }
}