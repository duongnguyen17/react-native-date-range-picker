package com.daterangepicker.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daterangepicker.R
import com.daterangepicker.adapter.MonthAdapter
import com.daterangepicker.base.AMCalendarRangeShortcut
import com.daterangepicker.base.AMCalendarShortcut
import com.daterangepicker.base.AMCalendarSingleShortcut
import com.daterangepicker.databinding.LayoutFragmentCalendarBinding
import com.daterangepicker.elements.MonthElement
import com.daterangepicker.enums.CalendarMode
import com.daterangepicker.interfaces.RangeSelectionListener
import com.daterangepicker.interfaces.SingleSelectionListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*
import android.graphics.Color


class CalendarFragment : BottomSheetDialogFragment() {

    private lateinit var binding: LayoutFragmentCalendarBinding
    private lateinit var calendarMode: CalendarMode
    private var monthElements: ArrayList<MonthElement> = arrayListOf()
    var singleSelectionListener: SingleSelectionListener? = null
    var rangeSelectionListener: RangeSelectionListener? = null

    @SuppressLint("InflateParams", "NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog!!.setOnShowListener {
            val bottomSheet: FrameLayout =
                dialog!!.findViewById(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT) // Thiết lập màu nền thành trong suốt
            val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
            behavior.skipCollapsed = true
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        binding = LayoutFragmentCalendarBinding.inflate(inflater, container, false)
        binding.textCancel.setOnClickListener {
            dialog?.dismiss()
        }
        binding.textSelect.setOnClickListener {
            dialog?.dismiss()
            val adapter = binding.recycler.adapter as MonthAdapter
            if (calendarMode == CalendarMode.SINGLE) {
                singleSelectionListener?.onSingleSelect(adapter.calSelection)
            } else if (calendarMode == CalendarMode.RANGE) {
                if (adapter.calSelectionEnd == null) {
                    rangeSelectionListener?.onRangeSelect(
                        adapter.calSelection,
                        adapter.calSelection
                    )
                } else {
                    rangeSelectionListener?.onRangeSelect(
                        adapter.calSelection,
                        adapter.calSelectionEnd
                    )
                }
            }
        }
        val currentArguments = arguments

        if (currentArguments != null) {
            val calendarTypeString = currentArguments.getString("calendarType")
            calendarMode = if (calendarTypeString != null) {
                CalendarMode.valueOf(calendarTypeString)
            } else {
                CalendarMode.SINGLE
            }

            if (currentArguments.containsKey("shortcuts")) {
                val shortcuts =
                    currentArguments.getSerializable("shortcuts") as Array<out AMCalendarShortcut>?
                if (shortcuts != null) {
                    handleShortcuts(inflater, shortcuts)
                }
            }
        }
        handleRecycler()
        handleConfig(currentArguments)

        return binding.root
    }

    private fun handleConfig(currentArguments: Bundle?) {
        if (currentArguments != null) {
            val adapter = binding.recycler.adapter as MonthAdapter

            if (currentArguments.containsKey("cal")) {
                handlePreselect(currentArguments)
            }
            if (currentArguments.containsKey("allowDateRangeChanges")) {
                adapter.allowDateRangeChanges = currentArguments.getBoolean("allowDateRangeChanges")
            }
            if (currentArguments.containsKey("textCancel")) {
                binding.textCancel.text =currentArguments.getString("textCancel")
            }
            if (currentArguments.containsKey("textSelect")) {
                binding.textSelect.text = currentArguments.getString("textSelect")
            }
            if (currentArguments.containsKey("textSelect")) {
                binding.textSelect.text = currentArguments.getString("textSelect")
            }
            if (currentArguments.containsKey("title")) {
                binding.textTitle.text = currentArguments.getString("title")
            }

        }

    }

    private fun handleRecycler() {
        val currentContext = context
        if (currentContext != null) {
            generateData()
            val adapter = MonthAdapter(monthElements)
            adapter.calendarMode = calendarMode
            if (calendarMode == CalendarMode.SINGLE) {
                adapter.singleSelectionListener = object : SingleSelectionListener {
                    override fun onSingleSelect(cal: Calendar?) {
//                        handleSingleSelection(cal)
                        if (cal != null) {
                            enableSelect()
                        }
                    }
                }
            } else if (calendarMode == CalendarMode.RANGE) {
                adapter.rangeSelectionListener = object : RangeSelectionListener {
                    override fun onRangeSelect(cal1: Calendar?, cal2: Calendar?) {
//                        handleRangeSelection(cal1, cal2)
                        if (cal1 != null) {
                            enableSelect()
                        }
                    }
                }
            }
            binding.recycler.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            binding.recycler.adapter = adapter

            val calNow = Calendar.getInstance()
            val pos =
                adapter.getPositionOfElement(calNow.get(Calendar.MONTH), calNow.get(Calendar.YEAR))
            (binding.recycler.layoutManager as LinearLayoutManager).scrollToPosition(pos)
        }
    }

    private fun handleShortcuts(
        inflater: LayoutInflater,
        shortcuts: Array<out AMCalendarShortcut>
    ) {
        binding.scrollShortcut.visibility = View.VISIBLE
        for (shortcut in shortcuts) {
            val itemShortcut = inflater.inflate(R.layout.item_shortcut, null)
            val btn = itemShortcut.findViewById<Button>(R.id.btn)
            btn.text = shortcut.text
            if (shortcut is AMCalendarSingleShortcut) {
                val cal = shortcut.cal
                if (cal != null) {
                    val time = cal.timeInMillis
                    btn.setTag(R.id.tag_amcalendar_time_selection, time)
                    btn.setOnClickListener { v ->
                        val tagTime =
                            v.getTag(R.id.tag_amcalendar_time_selection) as Long
                        val calTagTime = Calendar.getInstance()
                        calTagTime.timeInMillis = tagTime
                        removeTime(calTagTime)
                        val adapter = binding.recycler.adapter as MonthAdapter
                        adapter.calSelection = calTagTime.clone() as Calendar
                        adapter.singleSelectionListener?.onSingleSelect(adapter.calSelection)
                        adapter.notifyDataSetChanged()
                        val pos = adapter.getPositionOfElement(
                            adapter.calSelection?.get(Calendar.MONTH)!!,
                            adapter.calSelection?.get(Calendar.YEAR)!!
                        )
                        (binding.recycler.layoutManager as LinearLayoutManager).scrollToPosition(
                            pos
                        )
                    }
                }
            } else if (shortcut is AMCalendarRangeShortcut) {
                val cal1 = shortcut.cal1
                val cal2 = shortcut.cal2
                if (cal1 != null && cal2 == null) {
                    val time = cal1.timeInMillis
                    btn.setTag(R.id.tag_amcalendar_time_selection, time)
                    btn.setOnClickListener { v ->
                        val tagTime =
                            v.getTag(R.id.tag_amcalendar_time_selection) as Long
                        val calTagTime = Calendar.getInstance()
                        calTagTime.timeInMillis = tagTime
                        removeTime(calTagTime)
                        val adapter = binding.recycler.adapter as MonthAdapter
                        adapter.calSelection = calTagTime.clone() as Calendar
                        adapter.calSelectionEnd = null
                        adapter.rangeSelectionListener?.onRangeSelect(
                            adapter.calSelection,
                            adapter.calSelectionEnd
                        )
                        adapter.notifyDataSetChanged()
                        val pos = adapter.getPositionOfElement(
                            adapter.calSelection?.get(Calendar.MONTH)!!,
                            adapter.calSelection?.get(Calendar.YEAR)!!
                        )
                        (binding.recycler.layoutManager as LinearLayoutManager).scrollToPosition(
                            pos
                        )
                    }
                } else if (cal1 != null && cal2 != null && cal2.timeInMillis > cal1.timeInMillis) {
                    val time = cal1.timeInMillis
                    val timeEnd = cal2.timeInMillis
                    btn.setTag(R.id.tag_amcalendar_time_selection, time)
                    btn.setTag(R.id.tag_amcalendar_time_selection_end, timeEnd)
                    btn.setOnClickListener { v ->
                        val tagTime =
                            v.getTag(R.id.tag_amcalendar_time_selection) as Long
                        val tagTimeEnd =
                            v.getTag(R.id.tag_amcalendar_time_selection_end) as Long
                        val calTagTime = Calendar.getInstance()
                        val calTagTimeEnd = Calendar.getInstance()
                        calTagTime.timeInMillis = tagTime
                        calTagTimeEnd.timeInMillis = tagTimeEnd
                        removeTime(calTagTime, calTagTimeEnd)
                        val adapter = binding.recycler.adapter as MonthAdapter
                        adapter.calSelection = calTagTime.clone() as Calendar
                        adapter.calSelectionEnd = calTagTimeEnd.clone() as Calendar
                        adapter.rangeSelectionListener?.onRangeSelect(
                            adapter.calSelection,
                            adapter.calSelectionEnd
                        )
                        adapter.notifyDataSetChanged()
                        val pos = adapter.getPositionOfElement(
                            adapter.calSelection?.get(Calendar.MONTH)!!,
                            adapter.calSelection?.get(Calendar.YEAR)!!
                        )
                        (binding.recycler.layoutManager as LinearLayoutManager).scrollToPosition(
                            pos
                        )
                    }
                }
            }
            binding.layoutShortcut.addView(itemShortcut)
        }

    }

    private fun handlePreselect(currentArguments: Bundle) {
        val currentCal = currentArguments.getSerializable("cal") as Calendar?
        if (currentCal != null) {
            removeTime(currentCal)
            val adapter = binding.recycler.adapter as MonthAdapter
            adapter.calSelection = currentCal.clone() as Calendar
            if (currentArguments.containsKey("calEnd")) {
                val currentCalEnd = currentArguments.getSerializable("calEnd") as Calendar?
                if (currentCalEnd != null) {
                    removeTime(currentCalEnd)
                    adapter.calSelectionEnd = currentCalEnd.clone() as Calendar
                }
                adapter.rangeSelectionListener?.onRangeSelect(
                    adapter.calSelection,
                    adapter.calSelectionEnd
                )
            } else {
                adapter.singleSelectionListener?.onSingleSelect(adapter.calSelection)
            }
            adapter.notifyDataSetChanged()
            val pos = adapter.getPositionOfElement(
                adapter.calSelection?.get(Calendar.MONTH)!!,
                adapter.calSelection?.get(Calendar.YEAR)!!
            )
            (binding.recycler.layoutManager as LinearLayoutManager).scrollToPosition(pos)
        }
    }

    private fun enableSelect() {
        val currentContext = context
        if (currentContext != null) {
            binding.textSelect.setTextColor(
                ContextCompat.getColor(
                    currentContext,
                    R.color.colorAMCalendarSelect
                )
            )
            binding.textSelect.isClickable = true
            binding.textSelect.isFocusable = true
            binding.textSelect.isEnabled = true
        }
    }

    private fun generateData() {
        monthElements.clear()
        for (j in 2015 until 2030 step 1) {
            for (i in 0 until 12 step 1) {
                val monthElement = MonthElement()
                monthElement.year = j
                monthElement.month = i
                monthElements.add(monthElement)
            }
        }
    }

    private fun removeTime(vararg cals: Calendar) {
        for (cal in cals) {
            cal.set(Calendar.MILLISECOND, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.HOUR_OF_DAY, 0)
        }
    }

//    @SuppressLint("NotifyDataSetChanged")
//    private fun handleHeader() {
//        val currentContext = context
//        if(currentContext != null) {
//            binding.textHeader.setTextColor(ContextCompat.getColor(currentContext, R.color.colorAMCalendarHeaderDisabled))
//            if(calendarMode == CalendarMode.SINGLE) {
//                binding.textHeader.setText(R.string.amcalendar_please_select_date)
//            }
//            else if(calendarMode == CalendarMode.RANGE) {
//                binding.textHeader.setText(R.string.amcalendar_please_select_date_range)
//            }
//            binding.imgHeaderClear.setOnClickListener {
//                val adapter = binding.recycler.adapter as MonthAdapter
//                adapter.calSelection = null
//                adapter.calSelectionEnd = null
//                if(adapter.calendarMode == CalendarMode.SINGLE) {
//                    adapter.singleSelectionListener?.onSingleSelect(adapter.calSelection)
//                }
//                else if(adapter.calendarMode == CalendarMode.RANGE) {
//                    adapter.rangeSelectionListener?.onRangeSelect(adapter.calSelection, adapter.calSelectionEnd)
//                }
//                adapter.notifyDataSetChanged()
//                disableSelect()
//            }
//        }
//    }
//    private fun handleSingleSelection(cal: Calendar?) {
//        val currentContext = context
//        if(currentContext != null) {
//            if(cal != null) {
//                val dateFormat: Format = DateFormat.getMediumDateFormat(context)
//                val pattern: String = (dateFormat as SimpleDateFormat).toLocalizedPattern()
//                val sdf = SimpleDateFormat(pattern, Locale.getDefault())
//                binding.textHeader.text = sdf.format(cal.time)
//                binding.textHeader.setTextColor(ContextCompat.getColor(currentContext, R.color.colorAMCalendarHeaderSelected))
//                binding.imgHeaderClear.visibility = View.VISIBLE
//            }
//            else {
//                binding.textHeader.setText(R.string.amcalendar_please_select_date)
//                binding.textHeader.setTextColor(ContextCompat.getColor(currentContext, R.color.colorAMCalendarHeaderDisabled))
//                binding.imgHeaderClear.visibility = View.GONE
//            }
//        }
//    }
//    private fun disableSelect() {
//        val currentContext = context
//        if(currentContext != null) {
//            binding.textSelect.setTextColor(ContextCompat.getColor(currentContext, R.color.colorAMCalendarDisabled))
//            binding.textSelect.isClickable = false
//            binding.textSelect.isFocusable = false
//            binding.textSelect.isEnabled = false
//        }
//    }
//    @SuppressLint("SetTextI18n")
//    private fun handleRangeSelection(cal1: Calendar?, cal2: Calendar?) {
//        val currentContext = context
//        if (currentContext != null) {
//            if (cal1 == null && cal2 == null) {
//                binding.textHeader.setText(R.string.amcalendar_please_select_date)
//                binding.textHeader.setTextColor(
//                    ContextCompat.getColor(
//                        currentContext,
//                        R.color.colorAMCalendarHeaderDisabled
//                    )
//                )
//                binding.imgHeaderClear.visibility = View.GONE
//            } else if (cal1 != null && cal2 == null) {
//                val dateFormat: Format = DateFormat.getMediumDateFormat(context)
//                val pattern: String = (dateFormat as SimpleDateFormat).toLocalizedPattern()
//                val sdf = SimpleDateFormat(pattern, Locale.getDefault())
//                binding.textHeader.text = sdf.format(cal1.time)
//                binding.textHeader.setTextColor(
//                    ContextCompat.getColor(
//                        currentContext,
//                        R.color.colorAMCalendarHeaderSelected
//                    )
//                )
//                binding.imgHeaderClear.visibility = View.VISIBLE
//            } else if (cal1 != null && cal2 != null) {
//                val dateFormat: Format = DateFormat.getMediumDateFormat(context)
//                val pattern: String = (dateFormat as SimpleDateFormat).toLocalizedPattern()
//                val sdf = SimpleDateFormat(pattern, Locale.getDefault())
//                binding.textHeader.text = sdf.format(cal1.time) + " - " + sdf.format(cal2.time)
//                binding.textHeader.setTextColor(
//                    ContextCompat.getColor(
//                        currentContext,
//                        R.color.colorAMCalendarHeaderSelected
//                    )
//                )
//                binding.imgHeaderClear.visibility = View.VISIBLE
//            }
//        }
//    }
}