package com.dapadz.field

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity.CENTER
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.setPadding
import androidx.core.view.updateLayoutParams


// TODO: TEXT SIZE
// TODO: UPDATE COLORS
// TODO: ADD DOC

class DatePickerSlider @JvmOverloads constructor(
    context : Context,
    attributeSet : AttributeSet? = null,
) : LinearLayout(context, attributeSet) {

    private val TAG = "DatePickerSlider"

    private val PRIVATE_WHEEL_FIELD = "mSelectorWheelPaint"
    private val PRIVATE_DIVIDER_FIELD = "mSelectionDividerHeight"
    private val PRIVATE_EDIT_TEXT_FIELD = "mInputText"

    private val DEFAULT_PADDING = 6.dp
    private val DEFAULT_PICKERS_MARGIN = 16.dp
    private val DEFAULT_LINE_PADDING = 16.dp
    private val BACKROUND_RADIUS = 5f.dp

    protected lateinit var mountNumberPicker : NumberPicker
    protected lateinit var yearNumberPicker : NumberPicker

    private var backgroundLineHeight = 0
    private val backgroundLinePaint = Paint().apply {
        color = Color.GRAY
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    init {
        initRootView()
        createMountNumberPickerView()
        createYearNumberPickerView()
        disableDefaultDividers()
        setPickerMargins(DEFAULT_PICKERS_MARGIN)
        initBackgroundLineHeight()
    }

    private fun initRootView() {
        orientation = HORIZONTAL
        gravity = CENTER
        clipToPadding = false
        setPadding(DEFAULT_PADDING)
        setWillNotDraw(false)
    }

    private fun createMountNumberPickerView() {
        mountNumberPicker = NumberPicker(context).apply {
            layoutParams = LayoutParams(
                WRAP_CONTENT,
                WRAP_CONTENT
            )
        }
        addView(mountNumberPicker)
    }

    private fun createYearNumberPickerView() {
        yearNumberPicker = NumberPicker(context).apply {
            layoutParams = LayoutParams(
                WRAP_CONTENT,
                WRAP_CONTENT
            )
        }
        addView(yearNumberPicker)
    }

    var yearMinValue : Int = 0
        set(value) {
            field = value
            yearNumberPicker.minValue = value
        }

    var yearMaxValue : Int = 0
        set(value) {
            field = value
            yearNumberPicker.maxValue = value
        }

    var mountMinValue : Int = 0
        set(value) {
            field = value
            mountNumberPicker.minValue = value
        }

    var mountMaxValue : Int = 0
        set(value) {
            field = value
            mountNumberPicker.maxValue = value
        }

    var mountDisplayedValues : Array<String> = arrayOf()
        set(value) {
            field = value
            mountNumberPicker.displayedValues = value
        }

    var wrapSelectorWheel : Boolean = false
        set(value) {
            field = value
            mountNumberPicker.wrapSelectorWheel = value
            yearNumberPicker.wrapSelectorWheel = value
        }

    var backgroundLineColor : Int = Color.GRAY
        set(value) {
            field = value
            backgroundLinePaint.color = value
        }

    fun setTextColor(@ColorRes res : Int) {
        val color = ContextCompat.getColor(context, res)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { //api 29
            mountNumberPicker.textColor = color
            yearNumberPicker.textColor = color
        } else {
            mountNumberPicker.setTextColorForOldApi(color)
            yearNumberPicker.setTextColorForOldApi(color)
        }
        invalidate()
    }

    private fun drawBackgroundLine(c : Canvas?) {
        if (c == null) return
        c.save()
        val top = (yearNumberPicker.measuredHeight / 2f) - (backgroundLineHeight / 2f) + (DEFAULT_PADDING / 2f)
        val bottom = top + backgroundLineHeight + (DEFAULT_PADDING / 2)
        val right = measuredWidth.toFloat() - DEFAULT_PADDING
        val left = DEFAULT_PADDING.toFloat()
        val rect = RectF(left, top, right, bottom)
        c.drawRoundRect(rect, BACKROUND_RADIUS, BACKROUND_RADIUS, backgroundLinePaint)
        c.restore()
    }

    private fun initBackgroundLineHeight() {
        val h = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)  //api 29
            yearNumberPicker.textSize.toInt()
        else yearNumberPicker.getTextHeight()
        backgroundLineHeight = h + DEFAULT_LINE_PADDING
        invalidate()
    }

    private fun setPickerMargins(margin : Int) {
        yearNumberPicker.updateLayoutParams<MarginLayoutParams> {
            setMargins(margin / 2, 0, 0, 0)
        }
        mountNumberPicker.updateLayoutParams<MarginLayoutParams> {
            setMargins(0, 0, margin / 2, 0)
        }
    }

    private fun disableDefaultDividers() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { //api 29
            mountNumberPicker.selectionDividerHeight = 0
            yearNumberPicker.selectionDividerHeight = 0
        } else {
            mountNumberPicker.hideSelectionDivider()
            yearNumberPicker.hideSelectionDivider()
        }
        invalidate()
    }

    /**
     * We can ignore DiscouragedPrivateApi or SoonBlockedPrivateApi
     * as other methods are implemented for the new API
     */
    @SuppressLint("SoonBlockedPrivateApi", "DiscouragedPrivateApi")
    private fun NumberPicker.getTextHeight() : Int {
        return try {
            val field = NumberPicker::class.java.getDeclaredField(PRIVATE_EDIT_TEXT_FIELD).apply {
                isAccessible = true
            }
            (field[this] as EditText).textSize.toInt()
        } catch (t : Throwable) {
            0
        }
    }

    /**
     * We can ignore DiscouragedPrivateApi or SoonBlockedPrivateApi
     * as other methods are implemented for the new API
     */
    @SuppressLint("SoonBlockedPrivateApi")
    private fun NumberPicker.hideSelectionDivider() {
        try {
            val field = NumberPicker::class.java.getDeclaredField(PRIVATE_DIVIDER_FIELD).apply {
                isAccessible = true
            }
            field.set(this, 0)
        } catch (t : Throwable) {
            Log.e(TAG, t.message ?: "Error")
        }
    }

    /**
     * We can ignore DiscouragedPrivateApi or SoonBlockedPrivateApi
     * as other methods are implemented for the new API
     */
    @SuppressLint("DiscouragedPrivateApi", "SoonBlockedPrivateApi")
    private fun NumberPicker.setTextColorForOldApi(color : Int) {
        try {
            val field = NumberPicker::class.java.getDeclaredField(PRIVATE_WHEEL_FIELD).apply {
                isAccessible = true
            }
            (field[this] as Paint).color = color
            children.forEach {
                (it as? EditText)?.setTextColor(color)
            }
        } catch (t : Throwable) {
            Log.e(TAG, t.message ?: "Error")
        }
    }

    override fun onDraw(canvas : Canvas?) {
        super.onDraw(canvas)
        drawBackgroundLine(canvas)
    }
}