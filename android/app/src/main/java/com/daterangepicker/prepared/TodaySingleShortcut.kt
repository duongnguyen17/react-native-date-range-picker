package com.daterangepicker.prepared

import android.content.Context
import com.daterangepicker.R
import com.daterangepicker.base.AMCalendarSingleShortcut
import java.util.*

class TodaySingleShortcut(context: Context): AMCalendarSingleShortcut() {

    init {
        cal = Calendar.getInstance()
        text = context.resources.getString(R.string.txt_template_today)
    }

}