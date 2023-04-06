package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class CallSetListDTO(  // 등록된 호출 목록 DTO
    @SerializedName("status") var status: Int,
    @SerializedName("msg") var msg: String,
    @SerializedName("callList") var callList: ArrayList<CallDTO>    // 호출 목록
)
