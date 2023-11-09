package com.wooriyo.pinmenumobileer.pos.model

import com.google.gson.annotations.SerializedName

data class KpnHeaderDTO(
    @SerializedName("resCd") val resCd: String,       // 결과 코드
    @SerializedName("resMsg") val resMsg: String,
    @SerializedName("traceNo") val traceNo: String,   // 전문추적번호 : YYYMMDDHHMISS + Random3자리
    @SerializedName("version") val version: String
)
