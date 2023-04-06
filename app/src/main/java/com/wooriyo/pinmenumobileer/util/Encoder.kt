package com.wooriyo.pinmenumobileer.util

import android.os.Build
import java.net.URLEncoder
import java.util.*

class Encoder {
    companion object {
        private var encoder: Base64.Encoder? = null

        fun encodeBase64(text : String): String {
            var result = ""

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if(encoder == null)
                    encoder = Base64.getEncoder()

                result = encoder!!.encodeToString(text.toByteArray())
            }
            return result
        }

        fun encodeUTF8(text:String) : String {
            return URLEncoder.encode(text, "UTF-8")
        }
    }
}