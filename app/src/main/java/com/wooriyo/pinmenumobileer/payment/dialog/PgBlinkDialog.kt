package com.wooriyo.pinmenumobileer.payment.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wooriyo.pinmenumobileer.BaseDialogFragment
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.DialogAlertBinding

class PgBlinkDialog: BaseDialogFragment() {
    lateinit var binding: DialogAlertBinding
    val TAG = "PgBlinkDialog"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogAlertBinding.inflate(layoutInflater)

        binding.content.text = getString(R.string.payment_set_pg_dialog_blink)
        binding.confirm.text = getString(R.string.back)

        binding.confirm.setOnClickListener { dismiss() }

        return binding.root
    }
}