package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class PgDetailDTO(
    @SerializedName("idx") var idx: Int,
    @SerializedName("name") var name: String,
    @SerializedName("gea") var gea: Int,
    @SerializedName("price") var price: Int,
    @SerializedName("cancel") var cancel: Int,
    @SerializedName("cardname") var cardname: String,
    @SerializedName("cardnum") var cardnum: String,
    @SerializedName("storeidx") var storeidx: Int,
    @SerializedName("tableNo") var tableNo: String,
    @SerializedName("ordcode_key") var ordcode_key: String,
    @SerializedName("regdt") var regdt: String
)