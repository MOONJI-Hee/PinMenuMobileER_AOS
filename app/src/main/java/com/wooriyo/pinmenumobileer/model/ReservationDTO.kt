package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class ReservationDTO(
    @SerializedName("idx") var idx : Int,               // 예약 idx
    @SerializedName("name") var name: String,           // 예약자 이름
    @SerializedName("memo") var memo: String,           // 예약 요청사항
    @SerializedName("tel") var tel: String,             // 예약자 전화번호
    @SerializedName("addr") var addr: String,           // 주소 (배송 시 사용)
    @SerializedName("reserdt") var reserdt: String,     // 예약일시 > 예약 등록한 날짜 X, 실제 방문 날짜 O
)