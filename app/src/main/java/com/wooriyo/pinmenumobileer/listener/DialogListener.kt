package com.wooriyo.pinmenumobileer.listener

interface DialogListener {
    fun onTimeSet(start: String, end: String) {}
    fun onNickSet(nick: String) {}
    fun onComplete(popup: Int) {}
}