package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class CallHistoryDTO (
    @SerializedName("idx") var idx: Int,
    @SerializedName("storeidx") var storeidx: Int,
    @SerializedName("tableNo") var tableNo: String,
    @SerializedName("glist") var clist: ArrayList<CallDTO>,
    @SerializedName("iscompleted") var iscompleted: Int,       // 0: 미완료, 1: 완료
    @SerializedName("regdt") var regDt: String
)