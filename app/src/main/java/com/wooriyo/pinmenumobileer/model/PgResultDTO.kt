package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class PgResultDTO(
    @SerializedName("status") var status: Int,
    @SerializedName("msg") var msg: String,
    @SerializedName("totalrows") var totalrows: Int,
    @SerializedName("pglist") var pgList: ArrayList<PgListDTO>
)