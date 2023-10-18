package com.wooriyo.pinmenumobileer.printer

import android.content.Context
import com.sam4s.printer.Sam4sBuilder
import com.sam4s.printer.Sam4sPrint

open class PrinterConnection(val mContext:Context) {

    var device_type = 0
    var mSam4sPrinter: Sam4sPrint? = null

    constructor(context: Context?, type: Int) : this(context!!) {
        device_type = type
    }

    fun setType(type: Int): PrinterConnection? {
        device_type = type
        return this
    }

    open fun OpenPrinter(): Boolean {
        return true
    }

    open fun ClosePrinter() {}

    open fun IsConnected(): Boolean {
        return true
    }

    open fun sendData(builder: Sam4sBuilder?): Int {
        var iret = -1
        try {
            iret = mSam4sPrinter!!.sendData(builder)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return iret
    }

    open fun ReceiveData(): String? {
        var result: String? = null
        try {
            result = mSam4sPrinter!!.ReceiveData()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    open fun getPrinterName(): String? {
        var result: String? = null
        try {
            result = mSam4sPrinter!!.printerName
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    open fun getPrinterStatus(): String? {
        var result: String? = null
        try {
            result = mSam4sPrinter!!.printerStatus
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
    companion object {
        val DEVTYPE_ETHERNET = 0
        val DEVTYPE_BLUETOOTH = 1
        val DEVTYPE_SERIAL = 2
        val DEVTYPE_USB = 4
    }
}