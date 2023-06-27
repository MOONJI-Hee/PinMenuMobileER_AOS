package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class PaySettingDTO(
    @SerializedName("status") var status: Int,
    @SerializedName("msg") var msg: String,
    @SerializedName("idx") var idx: Int,
    @SerializedName("qrbuse") var qrbuse: String,       // QR코드결제 사용여부
    @SerializedName("cardbuse") var cardbuse: String,   // 카드결제 사용여부
    @SerializedName("mid") var mid: String,             // 나이스페이먼츠 상점관리자 mid
    @SerializedName("mid_key") var mid_key: String,     // 나이스페이먼츠 상점관리자 key
)