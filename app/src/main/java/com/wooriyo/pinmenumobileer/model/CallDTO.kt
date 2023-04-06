package com.wooriyo.pinmenumobileer.model

import java.io.Serializable

data class CallDTO(
    var idx: Int,
    var name: String,
    var gea : Int               // callHistory에서는 개수, callSetList에서는 seq
){ constructor(): this(0, "", 0) }