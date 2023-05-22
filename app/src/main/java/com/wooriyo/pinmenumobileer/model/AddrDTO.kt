package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class AddrDTO (
    @SerializedName("address_name") var address_name: String,
    @SerializedName("x") var x: String,
    @SerializedName("y") var y: String,
    @SerializedName("address_type") var address_type: String
)