package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class PrintListDTO (
    @SerializedName("status") var status: Int,
    @SerializedName("msg") var msg: String,
    @SerializedName("myprintList") var myprintList: ArrayList<PrintDTO>
)