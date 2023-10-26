package com.wooriyo.pinmenumobileer.util

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.graphics.Outline
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewDebug.IntToString
import android.view.ViewOutlineProvider
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import androidx.recyclerview.widget.RecyclerView
import com.sam4s.io.ethernet.SocketInfo
import com.sam4s.printer.Sam4sFinder
import com.sewoo.request.android.RequestHandler
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.MyApplication.Companion.bluetoothAdapter
import com.wooriyo.pinmenumobileer.MyApplication.Companion.remoteDevices
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.config.AppProperties
import com.wooriyo.pinmenumobileer.model.OrderDTO
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.printer.sam4s.EthernetConnection
import com.wooriyo.pinmenumobileer.printer.sam4s.NetworkPrinterInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.reflect.Method
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

// 자주 쓰는 메소드 모음 - 문지희 (2023.05 갱신)
class AppHelper {
    val TAG = "AppHelper"
    companion object {
        private val datetimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        private val appCallFormatter = DateTimeFormatter.ofPattern("yyMMddHHmmss")
        private val emailReg = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$".toRegex()
        private val pwReg = "^(?=.*[a-zA-Z0-9]).{8,}$".toRegex()

        // 네비게이션바 숨기기
        fun hideInset(activity: Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                activity.window.insetsController?.hide(android.view.WindowInsets.Type.navigationBars())
            }else {
                activity.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            }
        }

        // 바깥 클릭했을 때 키보드 내리기
        fun hideKeyboard(context: Context, focusView: View?, ev: MotionEvent) {
            if (focusView != null) {
                val rect = Rect()
                focusView.getGlobalVisibleRect(rect)
                val x = ev.x.toInt()
                val y = ev.y.toInt()
                if (!rect.contains(x, y)) {
                    val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(focusView.windowToken, 0)
                    focusView.clearFocus()
                }
            }
        }

        fun verifyEmail(email: String):Boolean {
            return email.matches(emailReg)
        }

        fun verifyPw(pw: String): Boolean {
            return pw.matches(pwReg)
        }

        val dec = DecimalFormat("00")
        val nformat = NumberFormat.getInstance()

        // 천 단위 콤마 찍기
        fun price(n: Int): String {
            return nformat.format(n)
        }

        // 한자리 수 n > 0n 형식으로 변환하기 + 빈 문자열 > 00으로 변환
        fun mkDouble(n: String): String {
            return if(n == "") {
                "00"
            } else {
                dec.format(n.toInt())
            }
        }

        // 코드에서 리사이클러뷰 높이 지정
        fun setRvHeight(rv: RecyclerView, size: Int, itemHeight: Int) {
            val hdp = size * itemHeight
            val hpx = (hdp * MyApplication.density).toInt()

            val params = rv.layoutParams
            params.height = hpx
            rv.layoutParams = params
        }

        fun setHeight(v: View, height: Int) {
            val hpx = (height * MyApplication.density).toInt()

            val params = v.layoutParams
            params.height = hpx
            v.layoutParams = params
        }

        // 이미지뷰 일부만 corner radius 주기
        fun getRoundedCornerLT(view: View, radius: Float) {
            view.outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View?, outline: Outline?) {
                    outline?.setRoundRect(0, 0, (view!!.width + radius).toInt(), (view.height + radius).toInt(), radius)
                }
            }
            view.clipToOutline = true
        }

        // 정수 > 00 형태의 문자열로 리턴
        fun intToString(n: Int): String {
            return if(n in 1..9) {"0$n"} else n.toString()
        }

        // 현재 날짜와 비교
        fun dateNowCompare(dt: String?): Boolean {    // 과거 : false, 현재 혹은 미래 : true
            return if(dt.isNullOrEmpty()) {
                false
            }else {
                val strDt = dt.replace(" ", "T")

                val now = LocalDateTime.now()
                val day = LocalDateTime.parse(strDt)

                val cmp = day.compareTo(now)

                cmp >= 0
            }
        }

        // 현재 일시 yyyy-mm-dd HH:mm:ss 형식으로 리턴
        fun getToday(): String {
            val now = LocalDateTime.now()
            return datetimeFormat.format(now).toString()
        }

        // APPCALL 거래번호 생성
        fun getAppCallNo(): String {
            return appCallFormatter.format(LocalDateTime.now())
        }

        fun osVersion(): Int = Build.VERSION.SDK_INT    // 안드로이드 버전
        fun versionName(context: Context): String = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        fun getPhoneModel(): String = Build.MODEL       // 디바이스 모델명

        fun leaveStore(activity: Activity) {
            ApiClient.service.leaveStore(MyApplication.useridx, MyApplication.storeidx, MyApplication.androidId)
                .enqueue(object : Callback<ResultDTO> {
                    override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                        Log.d("AppHelper", "매장 나가기 url : $response")
                        if(!response.isSuccessful) return

                        val result = response.body() ?: return
                        if(result.status == 1){
                            MyApplication.storeidx = 0
                            activity.finish()
                        }else
                            Toast.makeText(activity, result.msg, Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                        Toast.makeText(activity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                        Log.d("AppHelper", "매장 나가기 오류 > $t")
                        Log.d("AppHelper", "매장 나가기 오류 > ${call.request()}")
                    }
                })
        }

        // 블루투스 & 세우전자 프린터기 연결 관련 메소드

        // 블루투스 기기 찾기
        fun searchDevice() {
            Log.d("AppHelper", "searchDevice 시작")
            MyApplication.bluetoothAdapter.startDiscovery()
        }

        // 블루투스 연결
        fun connDevice(position: Int): Int {
            var retVal: Int = -1

            Log.d("AppHelper", "블루투스 기기 커넥트")
            Log.d("AppHelper", "remote 기기 > $remoteDevices")

            if(remoteDevices.isNotEmpty()) {
                val connDvc = remoteDevices[position]
                Log.d("AppHelper", "connDvc >> $connDvc")

                try {
                    MyApplication.bluetoothPort.connect(connDvc)
                    retVal = Integer.valueOf(0)
                } catch (e: IOException) {
                    e.printStackTrace()
                    retVal = Integer.valueOf(-1)
                }
            }else {
                retVal = -2
            }
            return retVal
        }

        // 페어링 된 기기 찾기
        fun getPairedDevice() : Int {
            Log.d("AppHelper", "getPairedDevice 시작")
            remoteDevices.clear()

            val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
            pairedDevices?.forEach { device ->
//            val deviceName = device.name
                val deviceHardwareAddress = device.address // MAC address

                if(MyApplication.bluetoothPort.isValidAddress(deviceHardwareAddress)) {
                    val deviceNum = device.bluetoothClass.majorDeviceClass

                    if(deviceNum == MyApplication.BT_PRINTER) {
                        remoteDevices.add(device)
                    }
                }
            }
            Log.d("AppHelper", "페어링된 기기 목록 >>$remoteDevices")

            return if(remoteDevices.isNotEmpty()) 1 else 0
        }

        // 주문내역(상세내역) 영수증 형태 String으로 받기 - 세우전자
        fun getPrint(ord: OrderDTO) : String {
            var hangul_size = AppProperties.HANGUL_SIZE_BIG
            var one_line = AppProperties.ONE_LINE_BIG
            var space = AppProperties.SPACE_BIG

            var total = 0.0

            val result: StringBuilder = StringBuilder()
            val underline1 = StringBuilder()
            val underline2 = StringBuilder()

            ord.name.forEach {
                if (total < one_line)
                    result.append(it)
                else if (total < (one_line * 2))
                    underline1.append(it)
                else
                    underline2.append(it)

                if (it == ' ') {
                    total++
                } else
                    total += hangul_size
            }

            val mlength = result.toString().length
            val mHangul = result.toString().replace(" ", "").length
            val mSpace = mlength - mHangul
            val mLine = mHangul * hangul_size + mSpace

            var diff = (one_line - mLine + 0.5).toInt()

            if (MyApplication.store.fontsize == 1) {
                if (ord.gea < 10) {
                    diff += 1
                    space = 4
                } else if (ord.gea >= 100) {
                    space = 1
                }
            } else if (MyApplication.store.fontsize == 2) {
                if (ord.gea < 10) {
                    diff += 1
                    space += 2
                } else if (ord.gea < 100) {
                    space += 1
                }
            }

            for (i in 1..diff) {
                result.append(" ")
            }
            result.append(ord.gea.toString())

            for (i in 1..space) {
                result.append(" ")
            }

            var togo = ""
            when (ord.togotype) {
                1 -> togo = "신규"
                2 -> togo = "포장"
            }
            result.append(togo)

            if (underline1.toString() != "")
                result.append("\n$underline1")

            if (underline2.toString() != "")
                result.append("\n$underline2")

            return result.toString()
        }

        // 주문내역(상세내역) 영수증 형태 String으로 받기 - SAM4S
        fun getSam4sPrint(ord: OrderDTO) : String {
            var hangul_size = AppProperties.HANGUL_SIZE_SAM4S
            var one_line = AppProperties.ONE_LINE_SAM4S
            var space = AppProperties.SPACE_SAM4S

            var total = 0.0

            val result: StringBuilder = StringBuilder()
            val underline1 = StringBuilder()
            val underline2 = StringBuilder()

            ord.name.forEach {
                if(total + hangul_size <= one_line)
                    result.append(it)
                else if(total + hangul_size <= (one_line * 2))
                    underline1.append(it)
                else
                    underline2.append(it)

                if(it == ' ') {
                    total++
                }else
                    total += hangul_size
            }

            val mlength = result.toString().length
            val mHangul = result.toString().replace(" ", "").length
            val mSpace = mlength - mHangul
            val mLine = mHangul * hangul_size + mSpace

            val diff = one_line - mLine + 1

            if (ord.gea >= 100) {
                space = 0
            }else if(ord.gea >= 10) {
                space = 1
            }

            for(i in 1..diff) {
                result.append(" ")
            }
            result.append(ord.gea.toString())

            for (i in 1..space) {
                result.append(" ")
            }

            var togo = ""
            when(ord.togotype) {
                1-> togo = "신규"
                2-> togo = "포장"
            }
            result.append(togo)

            if(underline1.toString() != "")
                result.append("\n$underline1")

            if(underline2.toString() != "")
                result.append("\n$underline2")

            return result.toString()
        }

        // SAM4S 프린터기 관련 메소드
        val finder: Sam4sFinder = Sam4sFinder()

        var scheduler = Executors.newSingleThreadScheduledExecutor()
        var future: ScheduledFuture<*>? = null

        var list : ArrayList<*>? = null

        // 같은 ip내 GCUBE 프린터 검색
        fun searchCube(context: Context): ArrayList<*>? {
//            lateinit var scheduler: ScheduledExecutorService
            Log.d("AppeHelper", "search 들어옴")

            if (future != null) {
                Log.d("AppHelper", "future is not null")
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
                finder.startSearch(context, Sam4sFinder.DEVTYPE_ETHERNET)
                Log.d("AppeHelper", "scheduler 돌려려려려려")
                future = it.scheduleWithFixedDelay(
                    Runnable {
                        list = finder.devices

                        if(list != null && list!!.size > 0) {
                            Log.d("AppeHelper", "device 찾음")
                            Log.d("AppeHelper", "프린터 왜 정보 안나와.. >>>> ${(list!![0] as SocketInfo).address}")
                            Log.d("AppeHelper", "프린터 왜 정보 안나와.. >>>> ${(list!![0] as SocketInfo).port}")

                            Log.d("AppeHelper", "프린터 정보 >>>> ${NetworkPrinterInfo(list!![0] as SocketInfo).getTitle()}")
                            Log.d("AppeHelper", "프린터 정보 >>>> ${NetworkPrinterInfo(list!![0] as SocketInfo).getSubTitle()}")
                            Log.d("AppeHelper", "프린터 정보 >>>> ${NetworkPrinterInfo(list!![0] as SocketInfo)}")

                            stopSearchCube()
//                            connectCube(context, NetworkPrinterInfo(list[0] as SocketInfo), list[0] as SocketInfo)
                        }
                    }, 0, 500, TimeUnit.MILLISECONDS )
            }

            return list
        }

        fun stopSearchCube() {
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

        fun connectCube(context: Context, info: SocketInfo) {
            MyApplication.INSTANCE.getPrinterConnection()?.ClosePrinter()
            val connection = EthernetConnection(context)
//        connection.setSocketInfo(info)
//        connection.setSocketInfo(printerInfo.getDevice())
            connection.setName(info.address)
            connection.setAdress(info.address)
            connection.setPort(info.port)
            connection.OpenPrinter()

            MyApplication.INSTANCE.setPrinterConnection(connection)

            checkCubeConn()
        }

        // GCUBE 연결되었는지 확인, 프린터기 모델명 return
        fun checkCubeConn(): String {
            var name = ""

            val temp = MyApplication.INSTANCE.mPrinterConnection

            if (temp != null && temp.IsConnected()) {
                Log.d("AppHelper", "연결됨 들어옴")
            }

            Log.d("AppHelper", temp.getPrinterStatus()?:"Null Error")
            name = temp.getPrinterName()?:""

            Log.d("AppHelper", "Printer name >> $name")

            return name
        }
    }
}