package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OptionDTO (
    @SerializedName("optidx") var idx : Int,
    @SerializedName("optnm") var name : String,
    @SerializedName("optcd") var optcd : String,
    @SerializedName("optreq") var optreq : Int,                     // 0 : 선택 옵션, 1 : 필수 옵션
    @SerializedName("optval") var optval : ArrayList<String>,
    @SerializedName("optpay") var optpay : ArrayList<Int>,
    @SerializedName("optmark") var optmark : ArrayList<String>,
):Serializable {
    constructor(type: Int) : this(0, "", "", type, ArrayList<String>(), ArrayList<Int>(), ArrayList<String>())
}