package com.wooriyo.pinmenumobileer.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenumobileer.MainTestActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.databinding.FragmentSelectStoreBinding
import com.wooriyo.pinmenumobileer.listener.ItemClickListener
import com.wooriyo.pinmenumobileer.printer.adapter.StoreAdapter

private const val arg_type = "type"

class SelectStoreFragment : Fragment() {
    lateinit var binding: FragmentSelectStoreBinding
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
        binding = FragmentSelectStoreBinding.inflate(layoutInflater)

        val storeAdapter = StoreAdapter(MyApplication.storeList)
        storeAdapter.setOnItemClickListener(object : ItemClickListener {
            override fun onItemClick(position: Int) {
                super.onItemClick(position)
                if(type == "pay")
//                    insPaySetting(position)
                else if (type == "print")
//                    insPrintSetting(position)
                else if (type == "qr") {

                }
            }
        })

        binding.rvStore.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.rvStore.adapter = storeAdapter

        return binding.root
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