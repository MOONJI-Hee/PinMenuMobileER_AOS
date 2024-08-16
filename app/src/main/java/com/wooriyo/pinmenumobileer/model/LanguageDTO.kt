package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class LanguageDTO(
    @SerializedName("lang") var lang : String,
    @SerializedName("img") var img : String,
    @SerializedName("isChecked") var isChecked : Boolean
)