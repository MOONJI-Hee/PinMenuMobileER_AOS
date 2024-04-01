package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class EventDTO (
    @SerializedName("status") var status: Int,
    @SerializedName("msg") var msg: String,
    @SerializedName("img") var img : String?,
    @SerializedName("content") var content : String,
    @SerializedName("link") var link : String,
    @SerializedName("buse") var buse : String
): Serializable
