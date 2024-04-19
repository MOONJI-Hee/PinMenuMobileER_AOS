package com.wooriyo.pinmenumobileer.more

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.MyApplication.Companion.pref
import com.wooriyo.pinmenumobileer.MyApplication.Companion.storeList
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.common.SelectStoreActivity
import com.wooriyo.pinmenumobileer.databinding.FragmentMoreBinding
import com.wooriyo.pinmenumobileer.util.AppHelper

class MoreFragment : Fragment() {
    lateinit var binding: FragmentMoreBinding
    val TAG = "MoreFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMoreBinding.inflate(layoutInflater)

        binding.run {
            userid.text = pref.getMbrDTO()?.userid
            arpayo.text = if (pref.getMbrDTO()?.arpayoid.isNullOrEmpty()) requireContext().getString(R.string.arpayo_dis_conn) else requireContext().getString(R.string.arpayo_conn)

            manual.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(Uri.parse("https://pinmenu.biz/file/pinmenu_manual_0325.pdf"), "application/pdf")
                }
                startActivity(Intent.createChooser(browserIntent, "Choose an Application"))
            }

            menuUi.setOnClickListener{
                when(storeList.size) {
                    0 -> Toast.makeText(context, R.string.msg_no_store, Toast.LENGTH_SHORT).show()
                    1 -> {
                        if((storeList[0].payuse == "Y" && AppHelper.dateNowCompare(storeList[0].endDate))) {
                            MyApplication.store = storeList[0]
                            MyApplication.storeidx = storeList[0].idx
                            startActivity(Intent(context, SetMenuUiActivity::class.java))
                        }else {
                            Toast.makeText(context, R.string.msg_no_pay, Toast.LENGTH_SHORT).show()
                        }
                    }
                    else ->  {
                        startActivity(
                            Intent(requireContext(), SelectStoreActivity::class.java).apply{ putExtra("type", "viewmode") }
                        )
                    }
                }
            }
            storeImg.setOnClickListener {
                when(storeList.size) {
                    0 -> Toast.makeText(context, R.string.msg_no_store, Toast.LENGTH_SHORT).show()
                    1 -> {
                        MyApplication.store = storeList[0]
                        MyApplication.storeidx = storeList[0].idx
                        startActivity(Intent(context, SetStoreImgActivity::class.java))
                    }
                    else ->  {
                        startActivity(
                            Intent(requireContext(), SelectStoreActivity::class.java).apply{ putExtra("type", "storeImg") }
                        )
                    }
                }
            }
            setting.setOnClickListener { requireContext().startActivity(Intent(requireContext(), SetActivity::class.java)) }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MoreFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}