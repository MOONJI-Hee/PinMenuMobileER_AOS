package com.wooriyo.pinmenumobileer.pos.model

import com.google.gson.annotations.SerializedName

data class KpnBodyDTO(
    @SerializedName("shopCd") val shopCd: String,       // 매장 코드
    @SerializedName("shopOrderNo") val shopOrderNo: String
)