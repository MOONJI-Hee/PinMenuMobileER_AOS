package com.wooriyo.pinmenumobileer.listener

interface OrderClickListener {
    fun onComplete(position:Int)
    fun onConfirm(position:Int)
    fun onDelete(position:Int)
    fun onPrint(position:Int)
    fun setTableNo(position:Int)
}