package com.wooriyo.pinmenumobileer.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.wooriyo.pinmenumobileer.config.AppProperties
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    val service: Api = initService()
    val imgService: Api = imgService()
//    val kakaoService: Api = kakaoService()

    private fun initService() : Api =
        Retrofit.Builder()
            .baseUrl(AppProperties.SERVER)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)

    private fun imgService() : Api =
        Retrofit.Builder()
            .baseUrl(AppProperties.IMG_SERVER)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)

//    fun kakaoService() : Api =
//        Retrofit.Builder()
//            .baseUrl(AppProperties.KAKAO_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(Api::class.java)
}