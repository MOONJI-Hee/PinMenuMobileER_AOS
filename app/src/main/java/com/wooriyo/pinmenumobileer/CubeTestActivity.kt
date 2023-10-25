package com.wooriyo.pinmenumobileer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.sam4s.io.ethernet.SocketInfo
import com.sam4s.printer.Sam4sBuilder
import com.sam4s.printer.Sam4sFinder
import com.wooriyo.pinmenumobileer.databinding.ActivityCubeTestBinding
import com.wooriyo.pinmenumobileer.member.StartActivity
import com.wooriyo.pinmenumobileer.printer.sam4s.EthernetConnection
import com.wooriyo.pinmenumobileer.printer.sam4s.NetworkPrinterInfo
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class CubeTestActivity : BaseActivity(), Runnable {
    lateinit var binding: ActivityCubeTestBinding

    private val finder: Sam4sFinder = Sam4sFinder()

    lateinit var scheduler: ScheduledExecutorService
    var future: ScheduledFuture<*>? = null

    val TAG = "CubeTestActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCubeTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        scheduler = Executors.newSingleThreadScheduledExecutor()

        binding.back.setOnClickListener {
            startActivity(Intent(this@CubeTestActivity, StartActivity::class.java))
        }

        binding.find.setOnClickListener { search() }
        binding.print.setOnClickListener { cut() }
        binding.check.setOnClickListener { binding.status.text = check() }
    }

    override fun onPause() {
        super.onPause()
        stop()
    }

    override fun onDestroy() {
        super.onDestroy()

        if(future != null) {
            future!!.cancel(false)
            while(!future!!.isDone()) {
                try {
                    Thread.sleep(500)
                } catch (e: Exception) {
                    break
                }
            }
            future = null
        }
        scheduler.shutdown()

    }

    fun check(): String {
        var result = "연결안됨"

        val temp = MyApplication.INSTANCE.mPrinterConnection


        if (temp != null && temp.IsConnected()) {
            result = "연결됨"
            Log.d(TAG, "연결됨 들어옴")
        }

        Log.d(TAG, temp.getPrinterStatus()?:"Null Error")
        Log.d(TAG, result)

        return result
    }

    fun search() {
        Log.d(TAG, "search 들어옴")
        if (future != null) {
            Log.d(TAG, "future is not null")
            future!!.cancel(false)
            while (!future!!.isDone) {
                try {
                    Thread.sleep(500)
                } catch (e: java.lang.Exception) {
                    break
                }
            }
            future = null
        }
        scheduler.let {
            finder.startSearch(this@CubeTestActivity, Sam4sFinder.DEVTYPE_ETHERNET)
            Log.d(TAG, "scheduler 돌려려려려려")
            future = it.scheduleWithFixedDelay(this, 0, 500, TimeUnit.MILLISECONDS )
        }
    }

    fun stop() {
        if (future != null) {
            future!!.cancel(false)
            while (!future!!.isDone) {
                try {
                    Thread.sleep(500)
                } catch (e: java.lang.Exception) {
                    break
                }
            }
            future = null
        }
        finder.stopSearch()
    }

    fun connect(printerInfo : NetworkPrinterInfo, info: SocketInfo) {
        Log.d(TAG, "connect 들어옴")

        Log.d(TAG, "Connect printer 정보 >>>> ${printerInfo.getDevice()?.address}")
        Log.d(TAG, "Connect printer 정보 >>>> ${printerInfo.getDevice()?.port}")

        Log.d(TAG, "Connect printer 정보 >>>> ${printerInfo.getTitle()}")
        Log.d(TAG, "Connect printer 정보 >>>> ${printerInfo.getSubTitle()}")

        MyApplication.INSTANCE.getPrinterConnection()?.ClosePrinter()
        val connection = EthernetConnection(this@CubeTestActivity)
//        connection.setSocketInfo(info)
//        connection.setSocketInfo(printerInfo.getDevice())
        connection.setName(info.address)
        connection.setAdress(info.address)
        connection.setPort(info.port)
        connection.OpenPrinter()

        MyApplication.INSTANCE.setPrinterConnection(connection)

//        val conn = Sam4sAndroidApplication.INSTANCE.getPrinterConnection()
//        conn?.ClosePrinter()
//        val  info:NetworkPrinterInfo = parent!!.getItemAtPosition(position) as NetworkPrinterInfo
//        val  connection = EthernetConnection(activity!!.baseContext)
//        connection.setSocketInfo(info.getDevice())
//        connection.OpenPrinter()
//        Sam4sAndroidApplication.INSTANCE.setPrinterConnection(connection)
//        val nextIntent =  Intent(activity!!.baseContext, MenuActivity::class.java)
//        startActivity(nextIntent)

    }

    fun print() {
//        builder.addText("Test Print 으악")
//        builder.addFeedUnit(1)
//
//        if(printer!=null) {
//            printer!!.sendData(builder)
//        }
    }

    override fun run() {
        val list = finder.devices

        if(list.size > 0) {
            Log.d(TAG, "device 찾음")
            Log.d(TAG, "프린터 왜 정보 안나와.. >>>> ${(list[0] as SocketInfo).address}")
            Log.d(TAG, "프린터 왜 정보 안나와.. >>>> ${(list[0] as SocketInfo).port}")

            Log.d(TAG, "프린터 정보 >>>> ${NetworkPrinterInfo(list[0] as SocketInfo).getTitle()}")
            Log.d(TAG, "프린터 정보 >>>> ${NetworkPrinterInfo(list[0] as SocketInfo).getSubTitle()}")
            Log.d(TAG, "프린터 정보 >>>> ${NetworkPrinterInfo(list[0] as SocketInfo)}")
            stop()
            connect(NetworkPrinterInfo(list[0] as SocketInfo), list[0] as SocketInfo)
        }
    }

    fun cut() {
        //Builder
        Log.d(TAG, "cut 들어옴")
        val builder  = Sam4sBuilder("GCube-100", 0)
        builder.addCut(Sam4sBuilder.CUT_FEED)
        MyApplication.INSTANCE.getPrinterConnection()!!.sendData(builder)
        builder.clearCommandBuffer()
    }

}