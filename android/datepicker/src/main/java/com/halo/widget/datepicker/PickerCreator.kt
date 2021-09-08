package com.halo.widget.datepicker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import java.util.*

object PickerCreator {

    fun createPicker(
        context: Context,
        view : View,
        spinnerTheme: Int,
        minDate: Calendar,
        maxDate: Calendar,
        defaultDate: Calendar,
        isDayShow: Boolean,
        isMonthShow: Boolean,
        isYearShow: Boolean,
        onDateChangedListener: OnDateChangedListener?
    ): PopupWindow {
        val layoutInflater = LayoutInflater.from(context)
        val viewInf = layoutInflater.inflate(R.layout.date_picker_popup_container, null)
        val viewGroup = viewInf.findViewById<ViewGroup>(R.id.parent_view)
        val btClose = viewInf.findViewById<ImageView>(R.id.close_bt)

        val popupYear =
            PopupWindow(view.width, ViewGroup.LayoutParams.WRAP_CONTENT)

        val pickYearView = DatePicker(context, spinnerTheme)
        pickYearView.setMinDate(minDate.timeInMillis)
        pickYearView.setMaxDate(maxDate.timeInMillis)
        pickYearView.init(
            defaultDate[Calendar.YEAR],
            defaultDate[Calendar.MONTH],
            defaultDate[Calendar.DAY_OF_MONTH],
            isDayShow,
            isMonthShow,
            isYearShow,
            onDateChangedListener
        )
        viewGroup.addView(
            pickYearView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupYear.contentView = viewInf
        popupYear.isOutsideTouchable = true
        btClose.setOnClickListener {
            popupYear.dismiss()
        }
        return popupYear
    }
}