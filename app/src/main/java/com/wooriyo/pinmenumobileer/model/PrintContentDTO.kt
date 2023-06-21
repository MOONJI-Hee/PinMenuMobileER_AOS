package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class PrintContentDTO(
    @SerializedName("status") var status: Int,
    @SerializedName("msg") var msg: String,
    @SerializedName("idx") var idx: Int,                    // bidx
    @SerializedName("os") var os: String,                   // 기기종류 I: 아이폰 , A: 안드로이드
    @SerializedName("admnick") var admnick: String,         // 관리자 기기 별명
    @SerializedName("nick") var nick: String,               // 프린터 기기 별명
    @SerializedName("model") var model: String,             // 프린터 모델명
    @SerializedName("printType") var printType: Int,        // 1: TS400B, 2: TE202, 3: Sam4S
    @SerializedName("fontSize") var fontSize: Int,          // 1: 크게, 2: 작게 (default 1)
    @SerializedName("kitchen") var kitchen: String,         // 주방프린터 Y:사용 N:미사용
    @SerializedName("receipt") var receipt: String,         // 고객 영수증 사용여부 주방프린터 Y:사용 N:미사용
    @SerializedName("ordcode") var ordcode: String,         // 주문번호 사용여부 주방프린터 Y:사용 N:미사용
    @SerializedName("category") var category: String ?= "",
    @SerializedName("blstatus") var blstatus: String        // 프린터 연결상태 (Y / N)
)
