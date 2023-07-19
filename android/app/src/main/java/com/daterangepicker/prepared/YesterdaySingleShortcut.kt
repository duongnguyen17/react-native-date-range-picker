package com.daterangepicker.prepared

import android.content.Context
import com.daterangepicker.R
import com.daterangepicker.base.AMCalendarSingleShortcut
import java.util.*

class YesterdaySingleShortcut(context: Context): AMCalendarSingleShortcut() {

    init {
        cal = Calendar.getInstance()
        cal?.add(Calendar.DAY_OF_MONTH, -1)
        text = context.resources.getString(R.string.txt_template_yesterday)
    }

}