package com.wooriyo.pinmenumobileer.pay.dialog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wooriyo.pinmenumobileer.BaseDialogFragment
import com.wooriyo.pinmenumobileer.MainActivity
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.DialogNoPayBinding

class NoPayDialog(val type: Int): BaseDialogFragment() {
    lateinit var binding: DialogNoPayBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogNoPayBinding.inflate(layoutInflater)

        var title = ""
        var content = ""

        when(type) {
            0 -> {  // QR일 때
                title = getString(R.string.order_pay_qr)
                content = getString(R.string.order_enable_qr)
            }
            1 -> {
                title = getString(R.string.title_card)
                content = getString(R.string.order_enable_card)
            }
        }

        binding.title.text = title
        binding.content.text = content

        binding.cancel.setOnClickListener { dismiss() }
        binding.go.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("type", 1)
            startActivity(intent)
            dismiss()
        }

        return binding.root
    }
}