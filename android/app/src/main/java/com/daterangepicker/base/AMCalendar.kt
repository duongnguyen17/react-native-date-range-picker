package com.daterangepicker.base

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.daterangepicker.enums.CalendarMode
import com.daterangepicker.fragments.CalendarFragment
import com.daterangepicker.interfaces.RangeSelectionListener
import com.daterangepicker.interfaces.SingleSelectionListener
import java.util.Calendar

class AMCalendar {

    companion object {

        fun singleSelect(
            activity: FragmentActivity,
            singleSelectionListener: SingleSelectionListener
        ): SingleAMCalendar {
            return SingleAMCalendar(activity.supportFragmentManager, singleSelectionListener)
        }

        fun rangeSelect(
            activity: FragmentActivity,
            rangeSelectionListener: RangeSelectionListener
        ): RangeAMCalendar {
            return RangeAMCalendar(activity.supportFragmentManager, rangeSelectionListener)
        }

    }

    class SingleAMCalendar(
        private val fragmentManager: FragmentManager,
        singleSelectionListener: SingleSelectionListener
    ) {

        private var singleSelectionListener: SingleSelectionListener? = null

        init {
            this.singleSelectionListener = singleSelectionListener
        }

        private var shortcuts: Array<out AMCalendarShortcut>? = null
        private var cal: Calendar? = null
        private var textCancel: String = "Cancel"
        private var textSelect: String = "Done"
        private var title :String = "Select date"

        fun shortcuts(vararg shortcuts: AMCalendarSingleShortcut): SingleAMCalendar {
            this.shortcuts = shortcuts
            return this
        }

        fun preselect(cal: Calendar): SingleAMCalendar {
            this.cal = cal
            return this
        }

        fun setTextCancel(txt: String):SingleAMCalendar {
            this.textCancel = txt
            return this
        }

        fun setTextSelect(txt: String):SingleAMCalendar {
            this.textSelect = txt
            return this
        }

        fun setTitle(txt: String):SingleAMCalendar {
            this.title = txt
            return this
        }

        fun show() {
            val calendarFragment = CalendarFragment()
            calendarFragment.singleSelectionListener = singleSelectionListener
            calendarFragment.rangeSelectionListener = null
            val bundle = Bundle()
            bundle.putString("calendarType", CalendarMode.SINGLE.toString())
            bundle.putSerializable("shortcuts", shortcuts)
            bundle.putString("textCancel", textCancel)
            bundle.putString("textSelect", textSelect)
            bundle.putString("title", title)
            if (cal != null) {
                bundle.putSerializable("cal", cal)
            }
            calendarFragment.arguments = bundle
            calendarFragment.show(fragmentManager, "AMCalendar")
        }

    }

    class RangeAMCalendar(
        private val fragmentManager: FragmentManager,
        rangeSelectionListener: RangeSelectionListener
    ) {

        private var rangeSelectionListener: RangeSelectionListener? = null

        init {
            this.rangeSelectionListener = rangeSelectionListener
        }

        private var shortcuts: Array<out AMCalendarShortcut>? = null
        private var cal: Calendar? = null
        private var calEnd: Calendar? = null
        private var allowDateRangeChanges: Boolean = false
        private var textCancel: String = "Cancel"
        private var textSelect: String = "Done"
        private var title :String = "Select date"


        fun shortcuts(vararg shortcuts: AMCalendarRangeShortcut): RangeAMCalendar {
            this.shortcuts = shortcuts
            return this
        }

        fun preselect(cal: Calendar, calEnd: Calendar): RangeAMCalendar {
            this.cal = cal
            this.calEnd = calEnd
            return this
        }

        fun setAllowDateRangeChanges(value: Boolean): RangeAMCalendar {
            this.allowDateRangeChanges = value
            return this
        }

        fun setTextCancel(txt: String):RangeAMCalendar {
            this.textCancel = txt
            return this
        }

        fun setTextSelect(txt: String):RangeAMCalendar {
            this.textSelect = txt
            return this
        }

        fun setTitle(txt: String):RangeAMCalendar {
            this.title = txt
            return this
        }

        fun show() {
            val calendarFragment = CalendarFragment()
            calendarFragment.singleSelectionListener = null
            calendarFragment.rangeSelectionListener = rangeSelectionListener
            val bundle = Bundle()
            bundle.putString("calendarType", CalendarMode.RANGE.toString())
            bundle.putSerializable("shortcuts", shortcuts)
            bundle.putBoolean("allowDateRangeChanges", allowDateRangeChanges)
            bundle.putString("textCancel", textCancel)
            bundle.putString("textSelect", textSelect)
            bundle.putString("title", title)
            if (cal != null) {
                bundle.putSerializable("cal", cal?.clone() as Calendar)
            }
            if (calEnd != null) {
                bundle.putSerializable("calEnd", calEnd?.clone() as Calendar)
            }
            calendarFragment.arguments = bundle
            calendarFragment.show(fragmentManager, "AMCalendar")
        }

    }

}