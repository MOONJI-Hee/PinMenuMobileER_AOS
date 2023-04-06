package com.wooriyo.pinmenumobileer.listener

interface ItemClickListener {
    fun onItemClick(position:Int) {}
    fun onItemMove(fromPos: Int, toPos: Int) {}
}