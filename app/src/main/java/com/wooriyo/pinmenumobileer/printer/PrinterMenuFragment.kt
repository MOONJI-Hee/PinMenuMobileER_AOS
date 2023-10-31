package com.wooriyo.pinmenumobileer.printer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sam4s.io.ethernet.SocketInfo
import com.sam4s.printer.Sam4sFinder
import com.wooriyo.pinmenumobileer.MainActivity
import com.wooriyo.pinmenumobileer.databinding.FragmentPrinterMenuBinding
import com.wooriyo.pinmenumobileer.util.AppHelper
import java.util.concurrent.TimeUnit

class PrinterMenuFragment : Fragment() {
    lateinit var binding: FragmentPrinterMenuBinding
    val TAG = "PrinterMenuFragment"

    var cubeList : ArrayList<*> ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).thread = Thread(Runnable{
            val reVal = AppHelper.getPairedDevice()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPrinterMenuBinding.inflate(layoutInflater)

        // 권한 확인
        (activity as MainActivity).checkPermissions()

        binding.run {
            connSet.setOnClickListener {
                val intent = Intent(context, SetConnActivity::class.java)
//                intent.putExtra("cubeList", cubeList)
//                Log.d("AppeHelper", "cubeList 보냄 >> $cubeList")
                startActivity(intent)
//                if(MyApplication.remoteDevices.isEmpty() && cubeList.isNullOrEmpty()) {
//                    startActivity(Intent(context, NewConnActivity::class.java))
//                }else {
//                    val intent = Intent(context, SetConnActivity::class.java)
//                    intent.putExtra("cubeList", cubeList)
//                    startActivity(intent)
//                }
            }
            support.setOnClickListener { startActivity(Intent(context, SupportPrinterActivity::class.java)) }
            contentSet.setOnClickListener { startActivity(Intent(context, ContentSetActivity::class.java)) }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        AppHelper.searchCube(requireContext())
    }

    override fun onPause() {
        super.onPause()
        AppHelper.stopSearchCube()
    }

    override fun onDestroy() {
        super.onDestroy()
        AppHelper.destroySearchCube()
    }

    companion object {
        @JvmStatic
        fun newInstance() = PrinterMenuFragment()
    }
}