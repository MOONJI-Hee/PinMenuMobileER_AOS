package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class PrintDTO (
    @SerializedName("idx") val idx : Int,
    @SerializedName("storeidx") val storeidx : Int,
    @SerializedName("model") val model : String,
    @SerializedName("printType") val printType : Int,
    @SerializedName("nick") val nick : String
)