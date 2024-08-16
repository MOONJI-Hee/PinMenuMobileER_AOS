package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class LangResultDTO(
    @SerializedName("status") var status: Int,
    @SerializedName("msg") var msg: String,
    @SerializedName("lang_use_eng") var eng_buse : String,
    @SerializedName("lang_use_spa") var spa_buse : String,
    @SerializedName("lang_use_kor") var kor_buse : String,
    @SerializedName("lang_use_chi") var chi_buse : String,
    @SerializedName("lang_use_jpn") var jpn_buse : String,
    @SerializedName("lang_use_hin") var hin_buse : String,
    @SerializedName("lang_use_fre") var fre_buse : String,
    @SerializedName("lang_use_ara") var ara_buse : String,
    @SerializedName("lang_use_heb") var heb_buse : String,
    @SerializedName("lang_use_ben") var ben_buse : String,
    @SerializedName("lang_use_rus") var rus_buse : String,
    @SerializedName("lang_use_por") var por_buse : String,
    @SerializedName("lang_use_tha") var tha_buse : String,
    @SerializedName("lang_use_vie") var vie_buse : String
) {
    constructor() : this(
        1, "", "Y", "N", "N", "N", "N", "N", "N",
        "N", "N", "N", "N", "N", "N", "N"
    )
}
