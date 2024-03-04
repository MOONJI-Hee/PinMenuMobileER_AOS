package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OptionDTO (
    @SerializedName("opt_idx") var idx : Int,
    @SerializedName("opt_title") var name : St                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      ring,
    @SerializedName("opt_req") var optreq : Int,                 // 0 : 선택 옵션, 1 : 필수 옵션
    @SerializedName("optlist") var optval : ArrayList<ValueDTO>
):Serializable {
    constructor(type: Int) : this(0, "", "", type, ArrayList<String>(), ArrayList<Int>(), ArrayList<String>())
}


"stropt": [
{
    "opt_title": "123",
    "opt_idx": 82,
    "opt_req": 1,
    "optlist": [
    {
        "optidx": 83,
        "optnm": "123",
        "optmark": "+",
        "optpay": ""
    },
    {
        "optidx": 84,
        "optnm": "717",
        "optmark": "+",
        "optpay": ""
    }
    ]
}
]