package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StoreDTO(
    @SerializedName("idx") var idx : Int,
    @SerializedName("pidx") var useridx : Int,
    @SerializedName("name") var name : String,
    @SerializedName("name2") var name2 : String,
    @SerializedName("buse") var buse : String,
    @SerializedName("payuse") var payuse : String,
    @SerializedName("paydt") var paydt : String,
    @SerializedName("ordCnt") var ordCnt: Int,
    @SerializedName("callCnt") var callCnt: Int,
    @SerializedName("address") var address : String,
    @SerializedName("Lclat") var lat : String,
    @SerializedName("Lclong") var long : String,
    @SerializedName("img") var img : String,
    @SerializedName("regdt") var regdt : String,
    @SerializedName("status") var status : String,
    @SerializedName("statusdt") var statusdt : String,
    @SerializedName("tel") var tel : String?,
    @SerializedName("sns") var sns : String?,
    @SerializedName("delivery") var delivery : String?="N",
    @SerializedName("parking") var parking : String?="N",
    @SerializedName("parkingadr") var parkingAddr : String?,
    @SerializedName("p_lat") var p_lat : String?,
    @SerializedName("p_long") var p_long : String?,
    @SerializedName("thema_color") var bgcolor : String,
    @SerializedName("thema_mode") var viewmode : String,
    @SerializedName("hbuse") var hbuse : String,
    @SerializedName("mon_buse") var mon_buse : String,
    @SerializedName("tue_buse") var tue_buse : String,
    @SerializedName("wed_buse") var wed_buse : String,
    @SerializedName("thu_buse") var thu_buse : String,
    @SerializedName("fri_buse") var fri_buse : String,
    @SerializedName("sat_buse") var sat_buse : String,
    @SerializedName("sun_buse") var sun_buse : String,
//    @SerializedName("workList")  var opentime: OpenTimeDTO?,
//    @SerializedName("breakList")  var breaktime: BrkTimeDTO?,
//    @SerializedName("holidayList")  var spcHoliday: ArrayList<SpcHolidayDTO>?
):Serializable {
    constructor(useridx: Int) :  this(0, useridx, "", "", "", "N", "", 0, 0,"", "", "", "", "", "", "", null, null, null, null, null, null, null,
        "d", "b", "N", "N", "N", "N", "N", "N", "N", "N")
}
