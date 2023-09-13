package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class QrListDTO(
    @SerializedName("status") var status: Int,
    @SerializedName("msg") var msg: String,
    @SerializedName("totalrows") var totalrows: Int,
    @SerializedName("qrCnt") var qrCnt: Int,
    @SerializedName("enname") var enname: String,   // 영문 매장명
    @SerializedName("qrlist") var qrList: ArrayList<QrDTO>
)
