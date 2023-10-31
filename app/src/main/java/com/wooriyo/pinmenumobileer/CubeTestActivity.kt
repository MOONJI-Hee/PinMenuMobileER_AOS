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
import com.wooriyo.pinmenumobileer.util.AppHelper
import com.wooriyo.pinmenumobileer.util.AppHelper.Companion.checkCubeConn
import com.wooriyo.pinmenumobileer.util.AppHelper.Companion.connectCube
import com.wooriyo.pinmenumobileer.util.AppHelper.Companion.stopSearchCube
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class CubeTestActivity : BaseActivity() {
    lateinit var binding: ActivityCubeTestBinding

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

        binding.find.setOnClickListener { AppHelper.searchCube(this@CubeTestActivity) }
        binding.print.setOnClickListener { cut() }
        binding.check.setOnClickListener {
//            checkCubeConn(this@CubeTestActivity)
            connectCube(this@CubeTestActivity, AppHelper.cubePrinterList[0])
        }
    }

    override fun onPause() {
        super.onPause()
        stopSearchCube()
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

    fun search() {
        Log.d(TAG, "search 들어옴")

        if (AppHelper.future != null) {
            Log.d("AppHelper", "future is not null")
            AppHelper.future!!.cancel(false)
            while (!AppHelper.future!!.isDone) {
                try {
                    Thread.sleep(500)
                } catch (e: java.lang.Exception) {
                    break
                }
            }
            AppHelper.future = null
        }

        AppHelper.scheduler.let {
            AppHelper.finder.startSearch(this@CubeTestActivity, Sam4sFinder.DEVTYPE_ETHERNET)
            AppHelper.future = it.scheduleWithFixedDelay(
                Runnable {
                    val cubeList = AppHelper.getCubeList()

                    if(cubeList != null && cubeList.size>0) {
                        connectCube(this@CubeTestActivity, cubeList[0] as SocketInfo)
                    }
                }, 0, 500, TimeUnit.MILLISECONDS )
        }
    }

//    fun stop() {
//        if (future != null) {
//            future!!.cancel(false)
//            while (!future!!.isDone) {
//                try {
//                    Thread.sleep(500)
//                } catch (e: java.lang.Exception) {
//                    break
//                }
//            }
//            future = null
//        }
//        finder.stopSearch()
//    }

//    fun connect(printerInfo : NetworkPrinterInfo, info: SocketInfo) {
//        Log.d(TAG, "connect 들어옴")
//
//        Log.d(TAG, "Connect printer 정보 >>>> ${printerInfo.getDevice()?.address}")
//        Log.d(TAG, "Connect printer 정보 >>>> ${printerInfo.getDevice()?.port}")
//
//        Log.d(TAG, "Connect printer 정보 >>>> ${printerInfo.getTitle()}")
//        Log.d(TAG, "Connect printer 정보 >>>> ${printerInfo.getSubTitle()}")
//
//        MyApplication.INSTANCE.getPrinterConnection()?.ClosePrinter()
//        val connection = EthernetConnection(this@CubeTestActivity)
//        connection.setSocketInfo(info)
////        connection.setSocketInfo(printerInfo.getDevice())
////        connection.setName(info.address)
////        connection.setAdress(info.address)
////        connection.setPort(info.port)
//        connection.OpenPrinter()
//
//        MyApplication.INSTANCE.setPrinterConnection(connection)
//
////        val conn = Sam4sAndroidApplication.INSTANCE.getPrinterConnection()
////        conn?.ClosePrinter()
////        val  info:NetworkPrinterInfo = parent!!.getItemAtPosition(position) as NetworkPrinterInfo
////        val  connection = EthernetConnection(activity!!.baseContext)
////        connection.setSocketInfo(info.getDevice())
////        connection.OpenPrinter()
////        Sam4sAndroidApplication.INSTANCE.setPrinterConnection(connection)
////        val nextIntent =  Intent(activity!!.baseContext, MenuActivity::class.java)
////        startActivity(nextIntent)
//
//    }

//    override fun run() {
//        Log.d(TAG, "Runnable 들어옴")
//
//        val list = finder.devices
//        Log.d(TAG, "list size >> ${list.size}")
//
//        if(list.size > 0) {
//            Log.d(TAG, "device 찾음")
//            Log.d(TAG, "프린터 왜 정보 안나와.. >>>> ${(list[0] as SocketInfo).address}")
//            Log.d(TAG, "프린터 왜 정보 안나와.. >>>> ${(list[0] as SocketInfo).port}")
//
//            Log.d(TAG, "프린터 정보 >>>> ${NetworkPrinterInfo(list[0] as SocketInfo).getTitle()}")
//            Log.d(TAG, "프린터 정보 >>>> ${NetworkPrinterInfo(list[0] as SocketInfo).getSubTitle()}")
//            Log.d(TAG, "프린터 정보 >>>> ${NetworkPrinterInfo(list[0] as SocketInfo)}")
//            stop()
//            connect(NetworkPrinterInfo(list[0] as SocketInfo), list[0] as SocketInfo)
//        }
//    }

//    var builder = Sam4sBuilder("GCube-100", Sam4sBuilder.LANG_KO)

    fun cut() {
        Log.d(TAG, "cut 들어옴")

//        MyApplication.cubeBuilder = Sam4sBuilder("GCube-100", Sam4sBuilder.LANG_KO)

        MyApplication.cubeBuilder.createCommandBuffer()

        MyApplication.cubeBuilder.addText("안녕하세요\n")
        MyApplication.cubeBuilder.addCut(Sam4sBuilder.CUT_FEED)

        MyApplication.INSTANCE.getPrinterConnection()?.sendData(MyApplication.cubeBuilder)
        MyApplication.cubeBuilder.clearCommandBuffer()

//        builder = Sam4sBuilder("GCube-100", Sam4sBuilder.LANG_KO)
//        builder.addTextSize(1, 1)
//        builder.addFeedLine(1)
//        builder.addTextStyle(false, false, false, Sam4sBuilder.COLOR_1)
//        builder.addText("안녕하세요\n")
//        builder.addCut(Sam4sBuilder.CUT_FEED)

//        MyApplication.INSTANCE.getPrinterConnection()?.sendData(MyApplication.cubeBuilder)
//        builder.clearCommandBuffer()
    }

}