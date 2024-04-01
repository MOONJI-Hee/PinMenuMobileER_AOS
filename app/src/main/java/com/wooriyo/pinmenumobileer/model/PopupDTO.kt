package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class PopupDTO (
    @SerializedName("sid") var sid: Int,
    @SerializedName("img") var img : String,
    @SerializedName("name") var name : String,
    @SerializedName("link") var link : String
)