package com.daterangepicker.prepared

import android.content.Context
import com.daterangepicker.R
import com.daterangepicker.base.AMCalendarRangeShortcut
import java.util.*

class TodayRangeShortcut(context: Context): AMCalendarRangeShortcut() {

    init {
        cal1 = Calendar.getInstance()
        cal2 = null
        text = context.resources.getString(R.string.txt_template_today)
    }

}