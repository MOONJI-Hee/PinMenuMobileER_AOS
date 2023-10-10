package com.wooriyo.pinmenumobileer.pg.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.wooriyo.pinmenumobileer.BaseDialogFragment
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.DialogConfirmBinding

class CancelDialog(val onClickListener: OnClickListener): BaseDialogFragment() {
    lateinit var binding: DialogConfirmBinding
    val TAG = "PgCancelDialog"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogConfirmBinding.inflate(layoutInflater)

        binding.content.text = getString(R.string.dialog_pg_cancel_info)

        binding.cancel.text = getString(R.string.back)
        binding.confirm.text = getString(R.string.payment_cancel)

        binding.cancel.setOnClickListener { dismiss() }
        binding.confirm.setOnClickListener {
            dismiss()
            onClickListener.onClick(it)
        }

        return binding.root
    }
}