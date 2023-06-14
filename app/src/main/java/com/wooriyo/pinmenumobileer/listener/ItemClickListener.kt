package com.wooriyo.pinmenumobileer.listener

import android.content.Intent
import android.widget.Button
import android.widget.CheckBox
import com.wooriyo.pinmenumobileer.model.StoreDTO

interface ItemClickListener {
    fun onItemClick(position:Int) {}
    fun onItemMove(fromPos: Int, toPos: Int) {}
    fun onStoreClick(storeDTO: StoreDTO, intent: Intent) {}
    fun onCheckClick(position: Int, v: CheckBox, isChecked : Boolean) {}
}