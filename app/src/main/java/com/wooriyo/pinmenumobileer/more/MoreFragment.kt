package com.wooriyo.pinmenumobileer.more

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.wooriyo.pinmenumobileer.MainActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.MyApplication.Companion.pref
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.common.dialog.AlertDialog
import com.wooriyo.pinmenumobileer.common.dialog.ConfirmDialog
import com.wooriyo.pinmenumobileer.common.SelectStoreActivity
import com.wooriyo.pinmenumobileer.databinding.FragmentMoreBinding
import com.wooriyo.pinmenumobileer.member.LoginActivity
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
//                val chooseIntent = Intent.createChooser(browserIntent, "Choose an Application")
//                chooseIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(Intent.createChooser(browserIntent, "Choose an Application"))
            }

            menuUi.setOnClickListener{

            }
            storeImg.setOnClickListener {  }
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