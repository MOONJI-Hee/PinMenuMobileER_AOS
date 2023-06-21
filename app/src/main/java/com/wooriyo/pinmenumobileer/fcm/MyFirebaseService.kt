package com.wooriyo.pinmenumobileer.fcm

import android.app.*
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sewoo.jpos.command.ESCPOSConst
import com.wooriyo.pinmenumobileer.MyApplication
import com.wooriyo.pinmenumobileer.MyApplication.Companion.escposPrinter
import com.wooriyo.pinmenumobileer.R
import com.wooriyo.pinmenumobileer.config.AppProperties
import com.wooriyo.pinmenumobileer.member.StartActivity
import com.wooriyo.pinmenumobileer.model.ReceiptDTO
import com.wooriyo.pinmenumobileer.util.ApiClient
import com.wooriyo.pinmenumobileer.util.AppHelper
import retrofit2.Call
import retrofit2.Response


class MyFirebaseService: FirebaseMessagingService() {
    val TAG = "MyFirebase"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        MyApplication.pref.setToken(token)
        Log.d(TAG, "new Token >> $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d(TAG, "message.data >> ${message.data}")
        Log.d(TAG, "message.notification >> ${message.notification}")

        createNotification(message)

        val ordCode = message.data["moredata"]

        ApiClient.service.getReceipt(ordCode.toString()).enqueue(object : retrofit2.Callback<ReceiptDTO>{
            override fun onResponse(call: Call<ReceiptDTO>, response: Response<ReceiptDTO>) {
                Log.d(TAG, "단건 주문 조회 URL : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return

                when(result.status) {
                    1 -> {
                        if(MyApplication.bluetoothPort.isConnected) {
                            val pOrderDt = result.regdt
                            val pTableNo = result.tableNo
                            val pOrderNo = ordCode

                            val hyphen_num = AppProperties.HYPHEN_NUM_BIG
                            val font_size = AppProperties.FONT_BIG

                            val hyphen = StringBuilder()    // 하이픈
                            for (i in 1..hyphen_num) {
                                hyphen.append("-")
                            }

                            escposPrinter.printAndroidFont(
                                result.storenm,
                                AppProperties.FONT_WIDTH,
                                AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
                            escposPrinter.printAndroidFont("주문날짜 : $pOrderDt",
                                AppProperties.FONT_WIDTH,
                                AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
                            escposPrinter.printAndroidFont("주문번호 : $pOrderNo",
                                AppProperties.FONT_WIDTH,
                                AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
                            escposPrinter.printAndroidFont("테이블번호 : $pTableNo",
                                AppProperties.FONT_WIDTH,
                                AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
                            escposPrinter.printAndroidFont(
                                AppProperties.TITLE_MENU,
                                AppProperties.FONT_WIDTH, AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
                            escposPrinter.printAndroidFont(hyphen.toString(),
                                AppProperties.FONT_WIDTH, font_size, ESCPOSConst.LK_ALIGNMENT_LEFT)

                            result.orderdata.forEach {
                                val pOrder = AppHelper.getPrint(it)
                                escposPrinter.printAndroidFont(pOrder,AppProperties.FONT_WIDTH, font_size, ESCPOSConst.LK_ALIGNMENT_LEFT)
                            }
                            escposPrinter.lineFeed(4)
                            escposPrinter.cutPaper()
                        }else {
                            Log.d(TAG, "프린트 연결 안됨")
                        }
                    }
                    else -> Toast.makeText(applicationContext, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ReceiptDTO>, t: Throwable) {
                Toast.makeText(applicationContext, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "단건 주문 조회 오류 >> $t")
                Log.d(TAG, "단건 주문 조회 오류 >> ${call.request()}")
            }
        })

    }

    private fun createNotification(message: RemoteMessage) {
        val channelId = "pinmenu_mobile_noti"
        val uri: Uri = Uri.parse("android.resource://com.wooriyo.pinmenumobileer/raw/customnoti.wav")
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(Notification.DEFAULT_ALL)
            .setContentTitle(message.notification!!.title)
            .setContentText(message.notification!!.body)
            .setSound(uri)
            .setVibrate(longArrayOf(100L, 100L, 100L)) //알림시 진동 설정 : 1초 진동, 1초 쉬고, 1초 진동
            .setContentIntent(createPendingIntent())
            .setDefaults(Notification.DEFAULT_ALL)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // 오레오 버전 이후에는 채널이 필요하다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Notice", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        // 알림 생성
        notificationManager.notify(1, builder.build())
    }

    private fun createPendingIntent () : PendingIntent {
        val intent = Intent(this, StartActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addParentStack(StartActivity::class.java)
        stackBuilder.addNextIntent(intent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        else
            return  stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT)
    }
}