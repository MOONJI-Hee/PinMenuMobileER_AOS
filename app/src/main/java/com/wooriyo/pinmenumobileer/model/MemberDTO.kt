package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class MemberDTO (
    var status : Int,
    var msg : String,
    @SerializedName("useridx") var useridx : Int,
    @SerializedName("userid") var userid : String,
    @SerializedName("name")  var name : String,
    @SerializedName("emplyr_id") var arpayoid: String ? = "",
//    var use_start : String ? = "",
//    var use_end : String ?= ""
)