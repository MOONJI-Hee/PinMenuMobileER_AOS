package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class PrintContentDTO(
    @SerializedName("status") var status: Int,
    @SerializedName("msg") var msg: String,
    @SerializedName("idx") var idx: Int,
    @SerializedName("nick") var nick: String,
    @SerializedName("os") var os: String,
    @SerializedName("printType") var printType: Int,
    @SerializedName("fontSize") var fontSize: Int,
    @SerializedName("kitchen") var kitchen: String,
    @SerializedName("receipt") var receipt: String,
    @SerializedName("ordcode") var ordcode: String,
    @SerializedName("category") var category: String ?= "",
)
