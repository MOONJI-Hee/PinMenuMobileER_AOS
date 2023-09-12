package com.wooriyo.pinmenumobileer.qr

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.store.StoreListFragment

class SetQrcodeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_set_qrcode, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = SetQrcodeFragment()
    }
}