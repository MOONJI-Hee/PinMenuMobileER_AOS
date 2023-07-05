package com.wooriyo.pinmenumobileer.order.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wooriyo.pinmenumobileer.BaseDialogFragment
import com.wooriyo.pinmenumobileer.databinding.DialogCompleteBinding
import com.wooriyo.pinmenumobileer.listener.DialogListener

class CompleteDialog: BaseDialogFragment() {
    lateinit var binding: DialogCompleteBinding
    lateinit var dialogListener: DialogListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogCompleteBinding.inflate(layoutInflater)

        binding.cancel.setOnClickListener { dismiss() }
        binding.complete.setOnClickListener {
            val popup = if(binding.visibility.isChecked) 1 else 0

            dialogListener.onComplete(popup)
            dismiss()
        }

        return binding.root
    }

    fun setOnCompleteListener(dialogListener: DialogListener)  {
        this.dialogListener = dialogListener
    }
}