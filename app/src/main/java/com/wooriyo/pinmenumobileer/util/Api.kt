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
        @Query("md") md: String
    ): Call<MemberDTO>

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

    // 주문 결제
    @GET("m/udtCompletedOrder.php")
    fun payOrder(
        @Query("storeidx") storeidx: Int,
        @Query("ordidx") ordidx: Int,
        @Query("iscomplated") iscomplated: String
    ): Call<ResultDTO>

    // 주문 삭제
    @GET("m/delete_order.php")
    fun deleteOrder(
        @Query("storeidx") storeidx: Int,
        @Query("ordidx") ordidx: Int
    ): Call<ResultDTO>

    // 주문 프린트
    @GET()
    fun printOrder(

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
}