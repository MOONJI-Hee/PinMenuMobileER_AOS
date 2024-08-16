package com.wooriyo.pinmenumobileer.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StoreDTO(
    @SerializedName("idx") var idx : Int,
    @SerializedName("pidx") var useridx : Int,
    @SerializedName("name") var name : String,
    @SerializedName("subname") var subname : String,
    @SerializedName("buse") var buse : String,
    @SerializedName("payuse") var payuse : String,
    @SerializedName("paydt") var paydt : String,
    @SerializedName("paydate") var paydate : String,
    @SerializedName("enddt") var endDate : String,
    @SerializedName("paytype") var paytype: Int,    // 0 : 요금제 없음 , 1: 기본 , 2: 비즈니스, 4: 클라우드나인
    @SerializedName("fontsize") var fontsize : Int, // 영수증 폰트 사이즈 1: 큰 폰트, 2: 작은 폰트
    @SerializedName("popup") var popup : Int,       // 카드리더기 관련 팝업 출력 여부 0 : 안보여줌 , 1 : 보여줌
    @SerializedName("ordCnt") var ordCnt: Int,
    @SerializedName("callCnt") var callCnt: Int,
    @SerializedName("pgCnt") var pgCnt: Int,        // 결제내역 갯수
    @SerializedName("menuCnt") var menuCnt: Int,
    @SerializedName("agree") var agree: String,     // QR오더 관련 이행보증보험 관련 동의 여부
    @SerializedName("mid") var mid : String,
    @SerializedName("mid_key") var mid_key : String,
    @SerializedName("pg_storenm") var pg_storenm : String,
    @SerializedName("pg_addr") var pg_addr : String,
    @SerializedName("pg_tel") var pg_tel : String,
    @SerializedName("pg_snum") var pg_snum : String,
    @SerializedName("pg_ceo") var pg_ceo : String,
    @SerializedName("blname") var blname : String,
    @SerializedName("blphone") var blphone : String,
    @SerializedName("bladdr") var bladdr : String,
    @SerializedName("bletc") var bletc : String,
    @SerializedName("blmemo") var blmemo : String,
    @SerializedName("memo") var memo : String,
    @SerializedName("qrbuse") var qrbuse : String,
    @SerializedName("address") var address : String,
    @SerializedName("Lclat") var lat : String,
    @SerializedName("Lclong") var long : String,
    @SerializedName("img") var img : String,
    @SerializedName("content") var content : String,    // store 설명
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
    @SerializedName("lang_use_kor") var kor_buse : String,
    @SerializedName("lang_use_usa") var eng_buse : String,
    @SerializedName("lang_use_esp") var esp_buse : String,
    @SerializedName("lang_use_chn") var chn_buse : String,
    @SerializedName("lang_use_jpn") var jpn_buse : String
//    @SerializedName("workList")  var opentime: OpenTimeDTO?,
//    @SerializedName("breakList")  var breaktime: BrkTimeDTO?,
//    @SerializedName("holidayList")  var spcHoliday: ArrayList<SpcHolidayDTO>?
):Serializable {
    constructor(useridx: Int) :  this(0, useridx, "", "", "", "N", "", "", "", 0, 1, 0, 0, 0, 0, 0, "N", "", "", "", "", "", "", "", "N", "N", "N", "N", "N", "", "", "", "", "", "", "", "", "", "", null, null, null, null, null, null, null,
        "d", "l", "N", "N", "N", "N", "N", "N", "N", "N", "Y", "N", "N", "N", "N")
}
