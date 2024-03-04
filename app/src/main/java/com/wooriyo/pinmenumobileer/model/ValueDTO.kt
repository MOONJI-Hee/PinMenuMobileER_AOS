package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class ValueDTO(
    @SerializedName("optidx") var idx : Int,
    @SerializedName("optnm") var name : String,
    @SerializedName("optcd") var optcd : String,
    @SerializedName("optreq") var optreq : Int,                     // 0 : 선택 옵션, 1 : 필수 옵션
    @SerializedName("optval") var optval : ArrayList<String>,
    @SerializedName("optpay") var optpay : ArrayList<Int>,
    @SerializedName("optmark") var optmark : ArrayList<String>,
)
