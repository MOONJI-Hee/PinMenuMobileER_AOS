package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class OrderHistoryDTO(
    @SerializedName("ordidx") var idx : Int,                        // 주문 idx
    @SerializedName("tableNo") var tableNo : String,                // 테이블 번호
    @SerializedName("glist") var olist : ArrayList<OrderDTO>,       // 주문 상세 리스트
    @SerializedName("regdt") var regdt : String,                    // 등록일시 (주문일시)
    @SerializedName("amount") var amount : Int,                     // 총 금액
    @SerializedName("pos") var pos : Int,                           // 포스 여부 (1 : 완료, 0 : 미완료)
    @SerializedName("isprint") var isprint : Int,                   // 프린트 여부 (1 : 완료, 0 : 미완료)
    @SerializedName("iscompleted") var iscompleted : Int            // 완료 여부 (1 : 완료, 0 : 미완료)
)
