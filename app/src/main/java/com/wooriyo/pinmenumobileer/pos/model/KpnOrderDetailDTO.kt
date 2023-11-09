package com.wooriyo.pinmenumobileer.pos.model

import com.google.gson.annotations.SerializedName

data class KpnOrderDetailDTO(
    @SerializedName("orderDtlNo") var orderDtlNo: String,           // 주문 순번
    @SerializedName("prodCd") var prodCd: String,                   // 상품코드 (사이드 속성은 AAA 등으로 설정, 사이드 선택 메뉴는 상품 코드로 요청) (사이드 속성의 경우 다중 선택 가능시 속성코드를 Append하여 셋팅 (ex) AAA;AAB  → AAAAAB)
    @SerializedName("prodNm") var prodNm: String,                   // 상품명
    @SerializedName("saleUprc") var saleUprc: String,               // 상품판매단가
    @SerializedName("orderQty") var orderQty: String,               // 주문 수량
    @SerializedName("saleAmt") var saleAmt: String,                 // 상품 판매 가격 (매출액) = 상품판매단가 * 주문수량
    @SerializedName("dcAmt") var dcAmt: String,                     // 할인 금액
    @SerializedName("dcmSaleUprc") var dcmSaleUprc: String,         // 상품 실제 결제 가격 (실매출액) = 상품 판매 가격 - 할인 금액
    @SerializedName("vatAmt") var vatAmt: String,                   // 부과세

//    @SerializedName("sdaInfo") var sdaInfo: String,                 // 사이드 메뉴 - 속성선택코드정보 (다중 선택인 경우, ";"로 구분)
//    @SerializedName("sdsInfo") var sdsInfo: String,                 // 사이드 메뉴 - 선택메뉴코드정보 (단건전송)

    @SerializedName("cookMemo") var cookMemo: String,               // 주방 메모
)
