package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class PgTableDTO(
    @SerializedName("idx") var idx: Int,
    @SerializedName("tableNo") var tableNo: String,
    @SerializedName("buse") var buse: String        // Y, N
)
