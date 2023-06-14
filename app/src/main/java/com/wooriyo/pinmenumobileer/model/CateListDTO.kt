package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class CateListDTO(
    @SerializedName("status") var status : Int,
    @SerializedName("msg") var msg : String,
    @SerializedName("category") var cateList: ArrayList<CategoryDTO>
)