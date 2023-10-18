package com.wooriyo.pinmenumobileer.printer

import com.sam4s.io.ethernet.SocketInfo

class NetworkPrinterInfo(val mSocketInfo: SocketInfo): PrinterInfo() {
    init {
        type = TYPE_ETHERNET
    }

    fun getDevice(): SocketInfo? {
        return mSocketInfo
    }

    override fun getTitle():String {
        return mSocketInfo.address
    }

    override fun getSubTitle():String {
        //return mSocketInfo.port as String
        return String.format("PORT: %d ", mSocketInfo.port)
    }

    override fun is203dpi():Boolean {
        var b203dpi = false
        if(printername.contains("150")) b203dpi = true
        if(printername.contains("gcube-102")) b203dpi = true
        return b203dpi
    }
}