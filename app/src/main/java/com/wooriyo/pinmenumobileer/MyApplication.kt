package com.wooriyo.pinmenumobileer

import android.app.Application
import android.os.Build
import android.provider.Settings
import com.wooriyo.pinmenumobileer.model.StoreDTO
import com.wooriyo.pinmenumobileer.model.SharedDTO

class MyApplication: Application() {
    companion object {
        lateinit var pref: SharedDTO
//        lateinit var db : AppDatabase

        var density = 1.0F

        val os = "A"
        var osver = 0
        lateinit var appver : String
        lateinit var md : String
        lateinit var deviceName: String

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
        deviceName = Settings.Secure.getString(contentResolver, "bluetooth_name") ?: ""

        density = resources.displayMetrics.density

        super.onCreate()
    }
}