package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ValueDTO(
    @SerializedName("optidx") var idx : Int,
    @SerializedName("optnm") var name : String,
    @SerializedName("optmark") var mark : String,
    @SerializedName("optpay") var price : String
): Serializable {
    constructor(): this(0, "", "+", "")
}