package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class OrderListDTO(
    @SerializedName("status") var status: Int,
    @SerializedName("msg") var msg: String,
    @SerializedName("orderlist") var orderlist: ArrayList<OrderHistoryDTO>
)
