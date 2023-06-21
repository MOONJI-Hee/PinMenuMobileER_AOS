package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class ReceiptDTO (
    @SerializedName("status") var status: Int,
    @SerializedName("msg") var msg: String,
    @SerializedName("orderdata") var orderdata : ArrayList<OrderDTO>,       // 주문 상세 리스트
    @SerializedName("regdt") var regdt : String,                            // 등록일시 (주문일시)
    @SerializedName("tableNo") var tableNo : String,                        // 테이블 번호
    @SerializedName("storenm") var storenm : String
)