package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class ResultDTO(
    @SerializedName("status") var status : Int,
    @SerializedName("msg") var msg : String,

    @SerializedName("review") var review : Int,
    @SerializedName("curver") var curver : String,          //최신 버전
    @SerializedName("update") var update : Int,             //강제 업데이트 설정값 (0:권장 업데이트, 1:강제 업데이트)
    @SerializedName("updatemsg") var updateMsg : String,    //업데이트 메세지

    @SerializedName("idx") var idx : Int,
    @SerializedName("useridx") var useridx : Int,
    @SerializedName("bidx") var bidx : Int,
    @SerializedName("qidx") var qidx : Int,
    @SerializedName("filepath") var filePath: String
)
