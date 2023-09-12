package com.wooriyo.pinmenumobileer.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenumobileer.MainTestActivity
import com.wooriyo.pinmenumobileer.MyApplication
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
                    (activity as MainTestActivity).insPaySetting(position)
                else if (type == "print")
                    (activity as MainTestActivity).insPrintSetting(position)
                else if (type == "qr") {
                    //TODO agree 판별해서 동의 페이지나 설정으로 가기
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