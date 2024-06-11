package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName
import com.wooriyo.pinmenumobileer.model.TableNoDTO

data class TableNoListDTO(
    @SerializedName("status") var status : Int,
    @SerializedName("msg") var msg : String,
    @SerializedName("tableNoList") var tableNoList : ArrayList<TableNoDTO>
)
