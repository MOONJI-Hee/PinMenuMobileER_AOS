package com.wooriyo.pinmenumobileer.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.wooriyo.pinmenumobileer.BaseDialogFragment
import com.wooriyo.pinmenumobileer.databinding.DialogAlertBinding

class AlertDialog(val content: String): BaseDialogFragment() {
    lateinit var binding: DialogAlertBinding
    val TAG = "AlerDialog"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogAlertBinding.inflate(layoutInflater)

        binding.content.text = content

        binding.confirm.setOnClickListener { dismiss() }

        return binding.root
    }

    fun show() {
        val activity = activity
        if(activity != null) {
            show(activity.supportFragmentManager, TAG)
        }
    }
}