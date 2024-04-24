package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName

data class PopupListDTO (
    @SerializedName("status") var status : Int,
    @SerializedName("msg") var msg : String,
    @SerializedName("popuplist") var popupList : ArrayList<PopupDTO>,
    @SerializedName("bannerList") var bannerList : ArrayList<PopupDTO>
)