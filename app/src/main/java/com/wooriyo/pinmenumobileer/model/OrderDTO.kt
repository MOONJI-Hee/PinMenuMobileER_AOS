package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class OrderDTO(
    @SerializedName("idx") var idx : Int,           // 상품 idx
    @SerializedName("name") var name: String,       // 상품 이름
    @SerializedName("gea") var gea : Int,           // 상품 개수
    @SerializedName("price") var price : Int,       // 상품 가격 (하나당 가격)
    @SerializedName("opt") var opt: String,         // 추가 옵션 ( val1 / val2 / ... )
)