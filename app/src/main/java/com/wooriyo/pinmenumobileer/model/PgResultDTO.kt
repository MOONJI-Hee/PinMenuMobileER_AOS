package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class PgResultDTO(
    @SerializedName("status") var status: Int,
    @SerializedName("msg") var msg: String,
    @SerializedName("totalrows") var totalrows: Int,
    @SerializedName("pglist") var pgHistoryList: ArrayList<PgHistoryDTO>,
    @SerializedName("blAll") var blAll: String, // 모든 테이블 사용 여부
    @SerializedName("tablelist") var tableList: ArrayList<PgTableDTO>
)