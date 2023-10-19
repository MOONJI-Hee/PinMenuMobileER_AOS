package com.wooriyo.pinmenumobileer.order.dialog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wooriyo.pinmenumobileer.BaseDialogFragment
import com.wooriyo.pinmenumobileer.MainActivity
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.DialogNoPayBinding
import com.wooriyo.pinmenumobileer.order.OrderListActivity

class SetNicepayDialog: BaseDialogFragment() {
    lateinit var binding: DialogNoPayBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DialogNoPayBinding.inflate(layoutInflater)

        binding.title.text = getString(R.string.qr_nicepay_join_done)
        binding.content.text = getString(R.string.qr_nicepay_join_done_info)
        binding.cancel.text = getString(R.string.back)

        binding.cancel.setOnClickListener {
            val intent = Intent(context, OrderListActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            dismiss()
        }
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