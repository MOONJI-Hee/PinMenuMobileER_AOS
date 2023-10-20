package com.wooriyo.pinmenumobileer.printer.sam4s

abstract class PrinterInfo {
    lateinit var  printername:String
    var type = 0

    abstract fun getTitle():String
    abstract fun getSubTitle():String
    abstract fun is203dpi():Boolean

    companion object {
        val TYPE_ETHERNET = 0
        val TYPE_BLUETOOTH = 1
        val TYPE_SERIAL = 2
        val TYPE_USB = 4
    }
}