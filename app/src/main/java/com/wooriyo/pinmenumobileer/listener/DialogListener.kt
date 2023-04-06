package com.wooriyo.pinmenumobileer.listener

import com.wooriyo.pinmenumobileer.model.SpcHolidayDTO

interface DialogListener {
    fun onTimeSet(start: String, end: String) {}
    fun onHolidaySet(data: SpcHolidayDTO) {}
}