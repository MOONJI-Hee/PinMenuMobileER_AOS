package com.wooriyo.pinmenumobileer

import android.app.Application
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.view.WindowManager
import com.wooriyo.pinmenumobileer.model.StoreDTO
import com.wooriyo.pinmenumobileer.model.SharedDTO

class MyApplication: Application() {
    companion object {
        lateinit var pref: SharedDTO
//        lateinit var db : AppDatabase

        var width = 0
        var height = 0
        var density = 1.0F

        val os = "A"
        var osver = 0
        lateinit var appver : String
        lateinit var md : String

        var useridx = 0
        var storeidx = 0
        lateinit var store: StoreDTO

        fun setStoreDTO() {
            store = StoreDTO(useridx)
        }
    }

    override fun onCreate() {
        pref = SharedDTO(applicationContext)
//        db = AppDatabase.getInstance(applicationContext)

        osver = Build.VERSION.SDK_INT
        appver = applicationContext.packageManager.getPackageInfo(applicationContext.packageName, 0).versionName
        md = Build.MODEL

        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            width = windowMetrics.bounds.width()
            height = windowMetrics.bounds.height()
        } else {
            val display = windowManager.defaultDisplay
            val realpoint = Point()
            display.getRealSize(realpoint) // or getSize(size)
            width = realpoint.x
            height = realpoint.y
        }

        density = resources.displayMetrics.density

        super.onCreate()
    }
}