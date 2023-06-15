package com.wooriyo.pinmenumobileer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.wooriyo.pinmenumobileer.listener.EasyCheckListener


class EasyCheckReceiver: BroadcastReceiver() {
    lateinit var onListener : EasyCheckListener


    fun setOnEasyCheckListener(_onListener: EasyCheckListener) {
        onListener = _onListener
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val b = intent!!.extras
        val iter: Iterator<String> = b!!.keySet().iterator()
        while (iter.hasNext()) {
            val key = iter.next()
            val value = b[key]
            Log.e("[BroadcastReceiver]", "[" + key + "] " + value.toString())
        }

        if(onListener != null) onListener.getIntent(intent);

    }
}