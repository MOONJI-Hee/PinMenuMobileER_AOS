package com.wooriyo.pinmenumobileer.printer

import android.content.Context
import com.sam4s.io.ethernet.SocketInfo
import com.sam4s.printer.Sam4sBuilder
import com.sam4s.printer.Sam4sPrint

class EthernetConnection(mContext: Context):PrinterConnection(mContext) {
    private var device_name: String? = null
    private var device_port = 6001
    private var mSocketInfo: SocketInfo? = null

    init {
        device_type = DEVTYPE_ETHERNET
    }

    fun setSocketInfo(info: SocketInfo?): EthernetConnection? {
        mSocketInfo = info
        device_name = mSocketInfo!!.address
        device_port = mSocketInfo!!.port
        return this
    }

    fun setName(name: String?): EthernetConnection? {
        device_name = name
        return this
    }

    fun setAdress(address: String?): EthernetConnection? {
        device_name = address
        return this
    }

    fun setPort(port: Int): EthernetConnection? {
        device_port = port
        return this
    }

    override fun OpenPrinter(): Boolean {
        if (mSam4sPrinter == null) mSam4sPrinter = Sam4sPrint()
        try {
            mSam4sPrinter!!.openPrinter(Sam4sPrint.DEVTYPE_ETHERNET, device_name, device_port)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }

    override fun ClosePrinter() {
        if (mSam4sPrinter != null) {
            mSam4sPrinter!!.closePrinter()
            mSam4sPrinter = null
        }
    }

    override fun IsConnected(): Boolean {
        return if (mSam4sPrinter != null) mSam4sPrinter!!.IsConnected(device_type) else false
    }

    override fun sendData(builder: Sam4sBuilder?): Int {
        var iret = -1
        try {
            if (mSam4sPrinter != null) {
                iret = mSam4sPrinter!!.sendData(builder)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return iret
    }

    override fun ReceiveData(): String? {
        var result: String? = null
        if (mSam4sPrinter != null) {
            try {
                result = mSam4sPrinter!!.ReceiveData()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return result
    }

    override fun getPrinterName(): String? {
        var result: String? = null
        var iret: Int
        try {
            if (mSam4sPrinter != null) {
                result = mSam4sPrinter!!.printerName
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    override fun getPrinterStatus(): String? {
        var result: String? = null
        var iret: Int
        try {
                result = mSam4sPrinter!!.printerStatus
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

}