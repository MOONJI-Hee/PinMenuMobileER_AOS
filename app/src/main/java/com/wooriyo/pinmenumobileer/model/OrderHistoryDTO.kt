package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class OrderHistoryDTO(
    @SerializedName("ordidx") var idx : Int,                        // 주문 idx
    @SerializedName("storeidx") var storeidx : Int,                 // 매장 idx
    @SerializedName("tableNo") var tableNo : String,                // 테이블 번호
    @SerializedName("ordcode") var ordcode : String,                // 주문 코드
    @SerializedName("glist") var olist : ArrayList<OrderDTO>,       // 주문 상세 리스트
    @SerializedName("regdt") var regdt : String,                    // 등록일시 (주문일시)
    @SerializedName("total") var total : Int,                       // 총 개수
    @SerializedName("amount") var amount : Int,                     // 총 금액
    @SerializedName("iscompleted") var iscompleted : Int,           // 완료 여부 (1 : 완료, 0 : 미완료)
    @SerializedName("complate_updt") var complate_updt : String     // 완료일시 (결제일시)
)