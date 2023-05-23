package com.wooriyo.pinmenumobileer.listener

import android.content.Intent
import com.wooriyo.pinmenumobileer.model.StoreDTO

interface ItemClickListener {
    fun onItemClick(position:Int) {}
    fun onItemMove(fromPos: Int, toPos: Int) {}
    fun onStoreClick(storeDTO: StoreDTO, intent: Intent) {}
}