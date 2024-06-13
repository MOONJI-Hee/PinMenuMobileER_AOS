package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class ReceiptDTO (
    @SerializedName("status") var status: Int,
    @SerializedName("msg") var msg: String,
    @SerializedName("reserType") var reserType: Int,                        // 0: 일반 주문, 1: 예약, 2: 포장
    @SerializedName("orderdata") var orderdata : ArrayList<OrderDTO>,       // 주문 상세 리스트
    @SerializedName("reserdata") var rlist : ArrayList<ReservationDTO>,     // 예약 정보
    @SerializedName("regdt") var regdt : String,                            // 등록일시 (주문일시)
    @SerializedName("tableNo") var tableNo : String,                        // 테이블 번호
    @SerializedName("storenm") var storenm : String
)