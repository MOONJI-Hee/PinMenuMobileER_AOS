package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class QrDTO(
    @SerializedName("idx") val idx : Int,
    @SerializedName("storeidx") val storeidx : Int,
    @SerializedName("seq") var seq : Int,
    @SerializedName("status") var status: Int,      // QR코드 0 : 사융불가 , 1: 사용가능
    @SerializedName("filepath") var filePath: String,
    @SerializedName("tableNo") var tableNo: String,
    @SerializedName("regdt") var regdt: String
):Serializable
