package com.wooriyo.pinmenumobileer.util

import com.wooriyo.pinmenumobileer.model.*
import com.wooriyo.pinmenumobileer.model.ResultDTO
import com.wooriyo.pinmenumobileer.model.StoreListDTO
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface Api {
    // 로그인
    @GET("checkmbr.php")
    fun checkMbr(
        @Query("userid") userid: String,
        @Query("pass") pass: String,
        @Query("push_token") push_token: String,
        @Query("os") os: String,
        @Query("osvs") osvs: Int,
        @Query("appvs") appvs: String,
        @Query("md") md: String,
        @Query("uuid") androidId : String
    ): Call<MemberDTO>

    @GET("m/regmbr.php")
    fun regMember(
        @Query("userid") userid: String,
        @Query("alpha_userid") arpayo_id: String,
        @Query("user_pwd") pw: String,
        @Query("push_token") push_token: String,
        @Query("os") os: String,
        @Query("osvs") osver: Int,
        @Query("appvs") appver: String,
        @Query("md") model: String
    ): Call<ResultDTO>

    //아이디 중복 체크
    @FormUrlEncoded
    @POST("m/checkid.php")
    fun checkId(
        @Field("userid") userid: String
    ): Call<ResultDTO>

    //알파요 ID 연동
    @GET("m/checkalpha.php")
    fun checkArpayo(
        @Query("userid") arpayoId: String
    ): Call<ResultDTO>

    // 로그아웃
    @GET("checkLogout.php")
    fun logout(
        @Query("userid") userid: String,
        @Query("storeidx") storeidx: Int
    ): Call<ResultDTO>

    // 비밀번호 찾기
    @GET()
    fun findPwd(

    ): Call<ResultDTO>

    // 비밀번호 변경
    @GET("m/udt_pass.php")
    fun changePwd(
        @Query("useridx") useridx: Int,
        @Query("pass") nowPwd: String,  // 기존 비밀번호
        @Query("pwd") newPwd: String,   // 새 비밀번호
    ): Call<ResultDTO>

    // 탈퇴
    @GET("m/leave.php")
    fun dropMbr(
        @Query("useridx") useridx: Int
    ): Call<ResultDTO>

    // 매장 목록 조회
    @GET("m/store.list.php")
    fun getStoreList(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: String?="" // null일 때 처리를 위해서 여기만 String
    ): Call<StoreListDTO>

    //매장 등록
    @GET("m/regstore.php")
    fun regStore(
        @Query("useridx") useridx: Int,
        @Query("storenm") storenm: String,
        @Query("addr") addr: String,                // 주소
        @Query("lclong") lclong: String,           // 매장 경도
        @Query("lclat") lclat: String                  // 매장 위도
    ): Call<ResultDTO>

    // 매장 이용자수 체크
    @GET("m/checkLimitedDevice.php")
    fun checkDeviceLimit(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("token") token: String,
        @Query("uuid") androidId : String
    ): Call<ResultDTO>

    // 매장 나갈 때 이용자수 차감
    @GET("m/leaveStore.php")
    fun leaveStore(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("uuid") androidId : String
    ): Call<ResultDTO>

    // 카테고리 목록 조희
    @GET("m/getcategory.php")
    fun getCateList(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
    ): Call<CateListDTO>

    // 새로운 주문 유무 확인 (status == 1 : 새로운 주문 있음)
    @GET("m/udtOrdStatus.php")
    fun getOrdStatus(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int
    ): Call<ResultDTO>

    // 주문 확인 처리
    @GET("m/udtOrdUpdate.php")
    fun udtOrdStatus(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int
    ): Call<ResultDTO>

    // 주문 목록(히스토리) 조회
    @GET("m/order.list.php")
    fun getOrderList(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int
    ): Call<OrderListDTO>

    // 단건 주문 조회 (푸시)
    @GET("m/get.receipt.php")
    fun getReceipt(
        @Query("ordcode") ordcode: String,  // 주문 코드
    ): Call<ReceiptDTO>

    // 주문 완료
    @GET("m/udtCompletedOrder.php")
    fun payOrder(
        @Query("storeidx") storeidx: Int,
        @Query("ordidx") ordidx: Int,
        @Query("iscompleted") iscompleted: String
    ): Call<ResultDTO>

    // 주문 삭제
    @GET("m/delete_order.php")
    fun deleteOrder(
        @Query("storeidx") storeidx: Int,
        @Query("ordidx") ordidx: Int
    ): Call<ResultDTO>

    // 새로운 직원 호출 유무 확인
    @GET("m/udtCallStatus.php")
    fun getCallStatus(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int
    ): Call<ResultDTO>

    // 직원 호출 확인 처리
    @GET("m/udtCallUpdate.php")
    fun udtCallStatus(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int
    ): Call<ResultDTO>

    // 직원 호출 히스토리 조회
    @GET("m/callHistory.php")
    fun getCallHistory(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int
    ): Call<CallListDTO>

    // 직원 호출 완료 처리 (확인과 다름)
    // 확인 : 새로운 호출이 있을 때 알림음을 끄고 확인했다는 것을 알리기 위한 Api
    // 완료 : 주문/호출 목록이 고객에게 완전히 다 제공되었다는 것을 알리기 위한 Api
    @GET("m/udtCompletedCall.php")
    fun completeCall(
        @Query("storeidx") storeidx: Int,
        @Query("ordidx") ordidx: Int,
        @Query("iscompleted") iscompleted: String
    ): Call<ResultDTO>

    // 직원 호출 등록된 목록 조회 >> (고객이 호출한 내역 아님!! 관리자가 등록했던 리스트 전체)
    @GET("m/call.list.php")
    fun getCallList(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int
    ): Call<CallSetListDTO>

    // 직원 호출 등록
    @GET("m/ins_call.php")
    fun insCall(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("name") callName : String
    ): Call<ResultDTO>

    // 직원 호출 수정
    @GET("m/udt_call")
    fun udtCall (
        @Query("useridx") useridx: Int,
        @Query("idx") callidx: Int,
        @Query("name") callName : String
    ): Call<ResultDTO>

    // 직원 호출 삭제
    @GET("m/del_call.php")
    fun delCall(
        @Query("useridx") useridx: Int,
        @Query("idx") callidx: Int
    ): Call<ResultDTO>

    // 영수증 프린터 관련 Api
    // 프린터 설정 최조 진입 시
    @GET("m/ins_print_setting.php")
    fun insPrintSetting(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("uuid") androidId : String
    ): Call<ResultDTO>

    // 등록한 프린터 목록
    @GET("m/connect_print_list.php")
    fun connPrintList(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("uuid") androidId : String
    ): Call<PrintListDTO>

    // 프린터 별명 설정
    @GET("m/print_nick.php")
    fun setPrintNick(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("uuid") androidId : String,
        @Query("nick") nick: String,
        @Query("type") type: Int        // 1 : 관리자 기기, 2 : 프린트 기기
    ): Call<ResultDTO>

    // 사용가능한 프린터 목록
    @GET("m/print_model_list.php")
    fun getSupportList(): Call<PrintModelListDTO>

    // 프린터 삭제
    @GET("m/del_print.php")
    fun delPrint(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("uuid") androidId : String,
        @Query("idx") idx: Int
    ): Call<ResultDTO>

    // 프린터 기기 선택
    @GET("m/udt_print_kind.php")
    fun udtPrintModel(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("uuid") androidId : String,
        @Query("printType") printType: Int,
        @Query("blstatus") blstatus : String
    ): Call<ResultDTO>

    // 프린터 출력 설정 불러오기
    @GET("m/getprintinfo.php")
    fun getPrintContentSet(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("uuid") androidId : String
    ): Call<PrintContentDTO>

    // 프린터 출력 설정 하기
    @GET("m/udt_print_setting.php")
    fun setPrintContent(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("uuid") androidId : String,
        @Query("idx") idx: Int,
        @Query("fontSize") fontSize: Int,   // 1: 크게 , 2 : 작게
        @Query("kitchen") kitchen: String,  // 주방영수증 사용 여부 (Y: 사용, N: 미사용)
        @Query("receipt") receipt: String,  // 고객영수증 사용 여부 (Y: 사용, N: 미사용)
        @Query("ordcode") ordcode: String,  // 주문번호 사용 여부 (Y: 사용, N: 미사용)
        @Query("cate") category: String
    ): Call<PrintContentDTO>

    // 프린터 연결 상태값 저장
    @GET("m/udt_print_connect_status.php")
    fun setPrintConnStatus (
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("uuid") androidId : String,
        @Query("idx") idx: Int,
        @Query("blstatus") blstatus: String
    ): Call<ResultDTO>

    // 카카오 지도 관련 api
    @GET("/v2/local/search/address.json")
    fun kakaoSearch(
        @Header("Authorization") key : String,
        @Query("query") query : String
    ): Call<KakaoResultDTO>
}