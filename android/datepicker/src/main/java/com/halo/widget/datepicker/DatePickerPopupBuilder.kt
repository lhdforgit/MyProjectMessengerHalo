package com.halo.widget.datepicker

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import java.util.*

class DatePickerPopupBuilder {
    private var context: Context? = null
    private var callBack: OnDateChangedListener? = null
    private var isDayShown = true
    private var isYearShow = true
    private var isMonthShow = true
    private var spinnerTheme = 0 //default theme
    private var viewAnchor : View? = null

    private var defaultDate: Calendar = GregorianCalendar(1980, 0, 1)
    private var minDate: Calendar = GregorianCalendar(1900, 0, 1)
    private var maxDate: Calendar = GregorianCalendar(2100, 0, 1)

    fun context(context: Context?): DatePickerPopupBuilder {
        this.context = context
        return this
    }

    fun callback(callBack: OnDateChangedListener?): DatePickerPopupBuilder {
        this.callBack = callBack
        return this
    }

    fun defaultDate(
        year: Int,
        monthIndexedFromZero: Int,
        day: Int
    ): DatePickerPopupBuilder {
        defaultDate = GregorianCalendar(year, monthIndexedFromZero, day)
        return this
    }

    fun minDate(year: Int, monthIndexedFromZero: Int, day: Int): DatePickerPopupBuilder {
        minDate = GregorianCalendar(year, monthIndexedFromZero, day)
        return this
    }

    fun minDate(date: Calendar?): DatePickerPopupBuilder {
        minDate = date!!
        return this
    }

    fun maxDate(year: Int, monthIndexedFromZero: Int, day: Int): DatePickerPopupBuilder{
        maxDate = GregorianCalendar(year, monthIndexedFromZero, day)
        return this
    }

    fun maxDate(date: Calendar?): DatePickerPopupBuilder {
        if (date != null) {
            maxDate = date
        }
        return this
    }

    fun showDaySpinner(showDaySpinner: Boolean): DatePickerPopupBuilder {
        isDayShown = showDaySpinner
        return this
    }

    fun showMonthSpinner(showMothSpinner: Boolean): DatePickerPopupBuilder {
        isMonthShow = showMothSpinner
        return this
    }

    fun showYearSpinner(showYearSpinner: Boolean): DatePickerPopupBuilder{
        isYearShow = showYearSpinner
        return this
    }

    fun setViewAnchor(viewAnchor : View): DatePickerPopupBuilder{
        this.viewAnchor = viewAnchor
        return this
    }

    fun build(): PopupWindow? {
        requireNotNull(context) { "Context must not be null" }
        require(maxDate.time.time > minDate.time.time) { "Max date is not after Min date" }
        if (context != null && viewAnchor != null){
            return PickerCreator.createPicker(
                context = context!!,
                view = viewAnchor!!,
                spinnerTheme = spinnerTheme,
                minDate = minDate,
                maxDate = maxDate,
                defaultDate = defaultDate,
                isDayShow = isDayShown,
                isMonthShow = isMonthShow,
                isYearShow = isYearShow,
                onDateChangedListener = callBack
            )
        }
        return null
    }

    fun showDropDown(){
        viewAnchor?.let {
            build()?.showAsDropDown(it, 0, -it.height, Gravity.CENTER)
        }
    }
}