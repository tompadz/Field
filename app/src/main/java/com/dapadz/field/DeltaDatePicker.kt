package com.dapadz.field

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import java.text.DateFormatSymbols
import java.util.*


// TODO: FIX MONTH
// TODO: ADD ATTR STILES

class DeltaDatePicker @JvmOverloads constructor(
    context : Context,
    attributeSet : AttributeSet? = null,
) : FrameLayout(context, attributeSet) {

    private val DEFAULT_START_YEAR = 1900
    private val DEFAULT_END_YEAR = 2100

    private lateinit var pickerSlider : DatePickerSlider

    private val tempCalendarDate = Calendar.getInstance()
    private val minCalendarDate = Calendar.getInstance()
    private val maxCalendarDate = Calendar.getInstance()
    private val shortMonths = DateFormatSymbols.getInstance(Locale.getDefault()).shortMonths

    private val isSomeMinDay get() = tempCalendarDate.get(Calendar.YEAR) == minCalendarDate.get(Calendar.YEAR) &&
            tempCalendarDate.get(Calendar.DAY_OF_YEAR) == minCalendarDate.get(Calendar.DAY_OF_YEAR)

    private val isSomeMaxDay get() = tempCalendarDate.get(Calendar.YEAR) == maxCalendarDate.get(Calendar.YEAR) &&
            tempCalendarDate.get(Calendar.DAY_OF_YEAR) == maxCalendarDate.get(Calendar.DAY_OF_YEAR)

    init {
        createSliderView()
    }

    private fun createSliderView() {
        pickerSlider = DatePickerSlider(context).apply {
            layoutParams = LayoutParams(
                MATCH_PARENT,
                WRAP_CONTENT
            )
            mountDisplayedValues = shortMonths
        }
        addView(pickerSlider)
    }

    var minDate: Long = 0L
        set(value) {
            field = value
            tempCalendarDate.timeInMillis = value
            if (isSomeMinDay) return  // Same day, no-op.
            minCalendarDate.timeInMillis = value
            updateRange()
        }

    var maxDate: Long = 0L
        set(value) {
            field = value
            tempCalendarDate.timeInMillis = value
            if (isSomeMaxDay) return  // Same day, no-op.
            maxCalendarDate.timeInMillis = value
            updateRange()
        }

    private fun updateRange() {
        with(pickerSlider) {
            yearMinValue = minCalendarDate.get(Calendar.YEAR)
            mountMinValue = minCalendarDate.get(Calendar.MONTH)
            yearMaxValue = maxCalendarDate.get(Calendar.YEAR)
            mountMaxValue = maxCalendarDate.get(Calendar.MONTH)
        }
    }

}