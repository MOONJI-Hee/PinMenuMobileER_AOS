package com.wooriyo.pinmenumobileer.config

class AppProperties {
        companion object {

                private const val REAL_SERVER: String = "http://app.pinmenu.biz/api/"
                private const val TEST_SERVER: String = "http://testapp.pinmenu.biz/api/"
                private const val WEB_SERVER: String = "http://pinmenu.biz/api/"

                const val SERVER: String = REAL_SERVER
        //        const val IMG_SERVER: String = "http://49.247.22.8/api/"
                const val IMG_SERVER: String = "http://img.pinmenu.biz/api/"
                const val CP_SERVER: String = "http://cp.wooriyo.com/api/"

                const val KAKAO_URL : String = "https://dapi.kakao.com"

                // 푸시 알림 아이디
                const val NOTIFICATION_ID_ORDER = 1

                // 푸시 알림 채널
                const val CHANNEL_ID_ORDER = "pinmenuer_mobile_order"
                const val CHANNEL_ID_CALL = "pinmenuer_mobile_call"

                // 리사이클러뷰 멀티뷰 사용시 타입
                const val VIEW_TYPE_COM = 0
                const val VIEW_TYPE_ADD = -1
                const val VIEW_TYPE_EMPTY = -2

                const val VIEW_TYPE_ORDER = 3
                const val VIEW_TYPE_CALL = 4

                // Activity Result Code
                const val RESULT_DELETE = 10
                const val RESULT_MODIFY = 11

                // 권한 관련
                const val REQUEST_LOCATION = 0
                const val REQUEST_ENABLE_BT = 1
                const val REQUEST_NOTIFICATION = 2
                const val REQUEST_STORAGE = 3

                // 세우 프린터 관련
                const val BT_PRINTER = 1536

                //프린트 관련
                const val FONT_BIG = 37
                const val FONT_SMALL = 28
                const val FONT_WIDTH = 512

                const val HANGUL_SIZE_BIG = 3.8
                const val HANGUL_SIZE_SMALL = 3.5
                const val HANGUL_SIZE_SAM4S = 2

                const val HANGUL_SIZE = HANGUL_SIZE_BIG

                const val ENG_SIZE_UPPER_BIG = 2.53
                const val ENG_SIZE_LOWER_BIG= 1.9
                const val ENG_SIZE_UPPER_SMALL = 2.4
                const val ENG_SIZE_LOWER_SMALL = 1.8

                const val ENG_SIZE_UPPER = ENG_SIZE_UPPER_BIG
                const val ENG_SIZE_LOWER= ENG_SIZE_LOWER_BIG

                const val NUM_SIZE_BIG= 2.3
                const val NUM_SIZE_SMALL= 1.8

                const val NUM_SIZE= NUM_SIZE_BIG

                const val ONE_LINE_BIG = 38
                const val ONE_LINE_SMALL = 53
                const val ONE_LINE_SAM4S = 31

                const val HYPHEN_NUM_BIG = 50
                const val HYPHEN_NUM_SMALL = 66
                const val HYPHEN_NUM_SAM4S = 42

                const val SPACE_BIG = 3
                const val SPACE_SMALL = 5
                const val SPACE_SAM4S = 2

                const val SPACE = SPACE_BIG

                const val TITLE_MENU = "메     뉴     명                          수량         금액  "
                const val TITLE_MENU_SAM4S = "메   뉴   명                   수량   금액"
        }
}