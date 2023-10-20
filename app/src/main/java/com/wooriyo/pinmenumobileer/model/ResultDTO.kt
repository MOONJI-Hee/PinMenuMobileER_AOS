package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class ResultDTO(
    @SerializedName("status") var status : Int,
    @SerializedName("msg") var msg : String,
    @SerializedName("idx") var idx : Int,
    @SerializedName("useridx") var useridx : Int,
    @SerializedName("bidx") var bidx : Int,
    @SerializedName("qidx") var qidx : Int,
    @SerializedName("filepath") var filePath: String
)
