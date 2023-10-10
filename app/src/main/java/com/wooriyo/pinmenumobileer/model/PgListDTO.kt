package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class PgListDTO(
    @SerializedName("title") var title: String,
    @SerializedName("glist") var pgDetailList: ArrayList<PgDetailDTO>
)