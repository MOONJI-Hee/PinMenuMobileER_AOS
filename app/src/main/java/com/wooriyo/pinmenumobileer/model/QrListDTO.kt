package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class QrListDTO(
    @SerializedName("status") var status: Int,
    @SerializedName("msg") var msg: String,
    @SerializedName("qrList") var qrList: ArrayList<QrDTO>
)
