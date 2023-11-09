package com.wooriyo.pinmenumobileer.pos.model

import com.google.gson.annotations.SerializedName
import org.json.JSONArray

data class KpnOrderDTO(
    @SerializedName("orderVendorId") var orderVendorId: String,     // 주문업체아이디 (최초 연동 시 Fiserv에서 발급)
    @SerializedName("orderVendorNm") var orderVendorNm: String,     // 주문업체명
    @SerializedName("orderSystemId") var orderSystemId: String,     // 주문시스템아이디
    @SerializedName("orderSystemNm") var orderSystemNm: String,     // 주문시스템명

    @SerializedName("orderNo") var orderNo: String,                 // 주문 번호 (핀메뉴 생성 고유 주문 번호) (최대 20자리)
    @SerializedName("orderType") var orderType: String,             // 주문 타입 (COUNTER_ORDER:카운터 주문, TABLE_ORDER:테이블 주문, PICK_UP:픽업 주문, DELIVERY:배달 주문)
    @SerializedName("saleYn") var saleYn: String,                   // 주문 구분 (Y : 주문, N : 취소)
    @SerializedName("pickTime") var pickTime: String,               // 픽업시간 (HHMM)
    @SerializedName("printTime") var printTime: String,             // 주방프린터 출력시간 (분단위)
    @SerializedName("orgOrderNo") var orgOrderNo: String,           // 주문취소 시 원거래 주문 번호 (주문 취소 시에만 전송)
    @SerializedName("orderDt") var orderDt: String,                 // 주문일시
    @SerializedName("dataCnt") var dataCnt: String,                 // 주문 상세 데이터 건수
//    @SerializedName("OrderDetailInfo") var OrderDetailInfo: ArrayList<KpnOrderDetailDTO>,   // 주문 상세 데이터
    @SerializedName("OrderDetailInfo") var OrderDetailInfo: JSONArray,   // 주문 상세 데이터

    @SerializedName("paymentTotAmt") var paymentTotAmt: String,     // 전체 결제 금액
    @SerializedName("paymentCnt") var paymentCnt: String,           // 주문 상세 데이터 건수
    @SerializedName("kitchenMemo") var kitchenMemo: String,         // 주방 메모
    @SerializedName("tableCd") var tableCd: String,                 // 테이블 코드 (POS ASP 등록 테이블 코드 3자리)
    @SerializedName("posOrderNo") var posOrderNo: String            // 포스 주문 번호 (추가 주문일 경우 기존 주문 번호)
)
