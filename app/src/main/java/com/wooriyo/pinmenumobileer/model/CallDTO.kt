package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class CallDTO(
    @SerializedName("idx") var idx: Int,
    @SerializedName("name") var name: String,
    @SerializedName("gea") var gea : Int               // callHistory에서는 개수, callSetList에서는 seq
){ constructor(): this(0, "", 0) }