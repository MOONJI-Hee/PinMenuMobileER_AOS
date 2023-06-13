package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class PrintModelDTO(
    @SerializedName("idx") val idx : Int,
    @SerializedName("title") val title : String,
    @SerializedName("model") val model : String,
    @SerializedName("recom") val recom : String,
    @SerializedName("seq") val seq : Int
)
