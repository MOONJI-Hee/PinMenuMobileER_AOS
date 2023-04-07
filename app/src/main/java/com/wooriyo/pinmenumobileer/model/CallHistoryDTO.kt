package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class CallHistoryDTO (
    @SerializedName("cidx") var idx: Int,
    @SerializedName("tableNo") var tableNo: String,
    @SerializedName("clist") var clist: ArrayList<CallDTO>,
    @SerializedName("iscompleted") var iscompleted: Int,       // 0: 미완료, 1: 완료
    @SerializedName("regdt") var regdt: String
)