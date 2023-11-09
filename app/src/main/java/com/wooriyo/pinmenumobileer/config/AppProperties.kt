package com.wooriyo.pinmenumobileer.config

class AppProperties {
        companion object {

                private const val REAL_SERVER: String = "http://app.pinmenu.biz/api/"
                private const val TEST_SERVER: String = "http://app.pinmenu.biz/api/"
                private const val WEB_SERVER: String = "http://pinmenu.biz/api/"

                const val SERVER: String = REAL_SERVER
        //        const val IMG_SERVER: String = "http://49.247.22.8/api/"
                const val IMG_SERVER: String = "http://img.pinmenu.biz/api/"

                const val KAKAO_URL : String = "https://dapi.kakao.com"

                // 푸시 알림 아이디
                const val NOTIFICATION_ID_ORDER = 1

                // 푸시 알림 채널
                const val CHANNEL_ID_ORDER = "pinmenuer_mobile_order"
                const val CHANNEL_ID_CALL = "pinmenuer_mobile_call"

                // 리사이클러뷰 멀티뷰 사용시 타입
                const val VIEW_TYPE_COM = 0
                const val VIEW_TYPE_ADD = 1
                const val VIEW_TYPE_EMPTY = 2

                // 권한 관련
                const val REQUEST_LOCATION = 0
                const val REQUEST_ENABLE_BT = 1
                const val REQUEST_NOTIFICATION = 2

                // 세우 프린터 관련
                const val BT_PRINTER = 1536

                //프린트 관련
                const val FONT_BIG = 37
                const val FONT_SMALL = 28
                const val FONT_WIDTH = 512

                const val HANGUL_SIZE_BIG = 3.6
                const val HANGUL_SIZE_SMALL = 3.5
                const val HANGUL_SIZE_SAM4S = 2

                const val ONE_LINE_BIG = 41
                const val ONE_LINE_SMALL = 53
                const val ONE_LINE_SAM4S = 31

                const val HYPHEN_NUM_BIG = 50
                const val HYPHEN_NUM_SMALL = 66
                const val HYPHEN_NUM_SAM4S = 42

                const val SPACE_BIG = 3
                const val SPACE_SMALL = 5
                const val SPACE_SAM4S = 2

                const val TITLE_MENU = "메     뉴     명                                수량     구분"
                const val TITLE_MENU_SAM4S = "메   뉴   명                   수량   구분"

                // POS 관련
                // 1. KPN (FirstPOS)
                const val KPN_SERVICE_ID_ORDER = "Order"
                const val KPN_API_ID_ORDER = "OrderDetail"

                const val KPN_VENDOR_ID = ""
                const val KPN_VENDOR_NM = ""
                const val KPN_SYSTEM_ID = ""
                const val KPN_SYSTEM_NM = ""

                const val KPN_ORDER_TYPE_TABLE = "TABLE_ORDER"
                const val KPN_ORDER_TYPE_PICK = "PICK_UP"
                const val KPN_ORDER_TYPE_DELI = "DELIVERY"
                const val KPN_ORDER_TYPE_COUNTER = "COUNTER_ORDER"
        }
}