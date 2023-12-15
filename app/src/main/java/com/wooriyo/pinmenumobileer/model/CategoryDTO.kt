package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CategoryDTO (
    @SerializedName("idx") var idx : Int,
    @SerializedName("pidx") var pidx : Int,
    @SerializedName("name") var name : String,
    @SerializedName("subname") var subname : String,
    @SerializedName("code") var code : String,
    @SerializedName("seq") var seq : Int,
    @SerializedName("buse") var buse : String,  // 사용 여부 Y: 사용(=숨기기X), N: 미사용(=숨기기)
    @SerializedName("checked") var checked: Int // 0: 선택 안됨, 1: 선택됨
): Serializable