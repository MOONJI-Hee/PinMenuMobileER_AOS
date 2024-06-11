package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class TableNoDTO(
    @SerializedName("idx") var idx : Int,
    @SerializedName("seq") var seq : Int,
    @SerializedName("tableNo") var no : String,
    var isChecked: Boolean = false
)
