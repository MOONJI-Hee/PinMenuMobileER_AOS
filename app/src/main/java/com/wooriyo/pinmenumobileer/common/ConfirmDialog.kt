package com.wooriyo.pinmenumobileer.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.wooriyo.pinmenumobileer.databinding.DialogConfirmBinding

class ConfirmDialog(val content: String, val btn: String, val onClickListener: View.OnClickListener): DialogFragment() {
    lateinit var binding: DialogConfirmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogConfirmBinding.inflate(layoutInflater)

        binding.content.text = content
        binding.confirm.text = btn

        binding.cancel.setOnClickListener { dismiss() }
        binding.confirm.setOnClickListener(onClickListener)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val window = dialog?.window ?: return
        val params = window.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
//        params.height = 600
        window.attributes = params
    }
}