package com.wooriyo.pinmenumobileer.pos.model

import com.google.gson.annotations.SerializedName

data class KpnResultDTO(
    @SerializedName("body") val body: KpnBodyDTO,
    @SerializedName("header") val header: KpnHeaderDTO
)
