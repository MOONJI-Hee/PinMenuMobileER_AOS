package com.wooriyo.pinmenumobileer.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wooriyo.pinmenumobileer.R

private const val arg_type = "type"

class SelectStoreFragment : Fragment() {
    private var type: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getString(arg_type)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_select_store, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(type: String) =
            SelectStoreFragment().apply {
                arguments = Bundle().apply {
                    putString(arg_type, type)
                }
            }
    }
}