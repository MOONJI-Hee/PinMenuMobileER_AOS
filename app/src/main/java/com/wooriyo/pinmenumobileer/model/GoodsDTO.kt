package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName
import java.io.File
import java.io.Serializable

data class GoodsDTO (
    @SerializedName("gidx") var idx: Int,
    @SerializedName("seq") var seq : Int,
    @SerializedName("gname") var name : String,
    @SerializedName("simple") var content : String?="",
    @SerializedName("category") var category : String,
    @SerializedName("cooking_time_min") var cooking_time_min: String,
    @SerializedName("cooking_time_max") var cooking_time_max: String,
    @SerializedName("price") var price: Int,
    @SerializedName("photo") var img1 : String?="",
    @SerializedName("photo2") var img2 : String?="",
    @SerializedName("photo3") var img3 : String?="",
    var file1 : File?,       // 메뉴 등록, 수정 시 미디어 저장을 위한 변수
    var file2 : File?,       // 메뉴 등록, 수정 시 이미지 저장을 위한 변수
    var file3 : File?,
    @SerializedName("adDisplay") var adDisplay : String,
    @SerializedName("icon") var icon: Int,                          // 메뉴 상태 > 1: None, 2: Hide, 3: Best, 4: Sold Out, 5: New
    @SerializedName("boption") var boption : String,
    @SerializedName("stropt") var opt : ArrayList<OptionDTO>?
):Serializable {
    constructor():this(0, 0,"", "", "", "0", "0", 0, "", "", "", null, null, null, "N", 1, "N", ArrayList<OptionDTO>())
}