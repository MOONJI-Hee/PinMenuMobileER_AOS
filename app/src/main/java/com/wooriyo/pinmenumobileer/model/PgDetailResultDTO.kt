package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class PgDetailResultDTO(
    @SerializedName("status") var status: Int,
    @SerializedName("msg") var msg: String,
    @SerializedName("cardname") var cardname: String,
    @SerializedName("amt") var amt: Int,
    @SerializedName("tid") var tid: String,
    @SerializedName("pay_regdt") var pay_regdt: String,
    @SerializedName("pglist") var pgDetailList: ArrayList<PgDetailDTO>
)