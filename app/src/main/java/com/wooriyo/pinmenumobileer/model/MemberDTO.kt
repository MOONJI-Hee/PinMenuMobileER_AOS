package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class MemberDTO (
    var status : Int,
    var msg : String,
    @SerializedName("useridx") var useridx : Int,
    @SerializedName("userid") var userid : String,
    @SerializedName("isAlpha") var isAlpha : String, // 알파요 연동 여부 - Y:연동, N:미연동
    @SerializedName("emplyr_id") var arpayoid: String ? = "",
    @SerializedName("gcube") var gcube: Int ? = 0
)