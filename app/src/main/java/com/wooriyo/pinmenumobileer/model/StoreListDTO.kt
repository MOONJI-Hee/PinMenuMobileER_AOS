package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName
import com.wooriyo.pinmenumobileer.model.StoreDTO

data class StoreListDTO(
    var status : Int,
    var msg : String,
    @SerializedName("totalrows") var total : Int,
    @SerializedName("storelist") var storeList : ArrayList<StoreDTO>
)
