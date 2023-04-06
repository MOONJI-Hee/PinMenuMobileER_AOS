package com.wooriyo.pinmenumobileer.config

class AppProperties {
    companion object {

        private const val TEST_SERVER: String = "http://app.pinmenu.biz/api/"
        private const val WEB_SERVER: String = "http://pinmenu.biz/api/"

        const val SERVER: String = TEST_SERVER
        const val IMG_SERVER: String = WEB_SERVER

        const val KAKAO_URL : String = "https://dapi.kakao.com"

        // 리사이클러뷰 멀티뷰 사용시 타입
        const val VIEW_TYPE_COM = 0
        const val VIEW_TYPE_ADD = 1

    }
}