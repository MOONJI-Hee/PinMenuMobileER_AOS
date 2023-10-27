package com.wooriyo.pinmenumobileer.printer

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.sam4s.io.ethernet.SocketInfo
import com.sewoo.request.android.RequestHandler
import com.wooriyo.pinmenumobileer.MainActivity
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.config.AppProperties
import com.wooriyo.pinmenumobileer.databinding.FragmentPrinterMenuBinding
import com.wooriyo.pinmenumobileer.model.PrintDTO
import com.wooriyo.pinmenumobileer.model.PrintListDTO
import com.wooriyo.pinmenumobileer.printer.sam4s.NetworkPrinterInfo
import com.wooriyo.pinmenumobileer.util.ApiClient
import com.wooriyo.pinmenumobileer.util.AppHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread

class PrinterMenuFragment : Fragment() {
    lateinit var binding: FragmentPrinterMenuBinding
    val TAG = "PrinterMenuFragment"

    lateinit var cubeList : ArrayList<*>

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
                intent.putExtra("cubeList", cubeList)
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
        cubeList = AppHelper.searchCube(requireContext()) ?: ArrayList<SocketInfo>()
        if(cubeList.size > 0) {
            Log.d("AppeHelper", "cubeList 들어옴")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = PrinterMenuFragment()
    }
}