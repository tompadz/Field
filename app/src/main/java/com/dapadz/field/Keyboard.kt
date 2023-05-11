package com.dapadz.field

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity.CENTER
import android.view.Gravity.FILL
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.HORIZONTAL
import android.widget.LinearLayout.VERTICAL
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.core.view.setMargins
import androidx.core.view.setPadding

class Keyboard @JvmOverloads constructor(
    context : Context,
    attributeSet : AttributeSet? = null,
) : FrameLayout(context, attributeSet) {

    private val LEFT_ACTION_ID : Byte = -1
    private val RIGHT_ACTION_ID : Byte = -2

    private lateinit var keyboardLinearLayout : LinearLayout

    private var actionClickListener: KeyboardActionClickListener? = null
    private var isLeftIconSetup = false
    private var isRightIconSetup = false

    private val keyboardArray = arrayOf(
        byteArrayOf(1,  2,  3),
        byteArrayOf(4,  5,  6),
        byteArrayOf(7,  8,  9),
        byteArrayOf(-1, 0, -2),
    )

    init {
        initializeKeyboardLinearLayout()
        generateKeyboardView()
    }

    private fun initializeKeyboardLinearLayout() {
        keyboardLinearLayout = LinearLayout(context).apply {
            layoutParams = LayoutParams(
                WRAP_CONTENT,
                WRAP_CONTENT,
                CENTER
            )
            minimumWidth = 260.dp
            orientation = VERTICAL
            weightSum = keyboardArray.size.toFloat()
            gravity = FILL
        }
        addView(keyboardLinearLayout)
    }

    fun setLeftActionIcon(@DrawableRes res: Int?) {
        isLeftIconSetup = res != null
        updateActionIcon(LEFT_ACTION_ID, res)
    }

    fun setRightActionIcon(@DrawableRes res: Int?) {
        isRightIconSetup = res != null
        updateActionIcon(RIGHT_ACTION_ID, res)
    }

    fun setActionClickListener(listener : KeyboardActionClickListener) {
        actionClickListener = listener
    }

    private fun updateActionIcon(id: Byte, @DrawableRes res: Int?) {
        val actionLinearLayout = keyboardLinearLayout.children.last() as LinearLayout
        actionLinearLayout.forEach {
            it as KeyboardKey
            if (it.key == id) it.setIcon(res)
        }
    }

    private fun generateKeyboardView() {
        keyboardArray.forEach { keys ->
            generateKeyboardLine(keys)
        }
    }

    private fun generateKeyboardLine(keys : ByteArray) {
        val ll = getKeyboardLineLayout()
        keys.forEach { key ->
            ll.addView(
                KeyboardKey(context, key)
            )
        }
        keyboardLinearLayout.addView(ll)
    }

    private fun getKeyboardLineLayout() : LinearLayout =
        LinearLayout(context).apply {
            layoutParams = LayoutParams(
                MATCH_PARENT,
                WRAP_CONTENT
            )
            weightSum = keyboardArray[0].size.toFloat()
            gravity = HORIZONTAL
        }

    private inner class KeyboardKey(
        context : Context,
        val key : Byte,
    ) : FrameLayout(context, null) {

        private val DEFAULT_PADDING = 16.dp
        private val DEFAULT_MARGIN = 1.dp

        private var textView : TextView? = null
        private var iconView : ImageView? = null

        val isActionButton get() = key == LEFT_ACTION_ID || key == RIGHT_ACTION_ID
        val isHaveIcon get() = iconView?.drawable != null
        val isHaveText get() = textView?.text?.isNotBlank()

        init {
            initializeRootView()
            if (isActionButton) createIconView() else createTextView()
            setupClickListener()
        }

        private fun initializeRootView() {
            layoutParams = LinearLayout.LayoutParams(
                MATCH_PARENT,
                MATCH_PARENT,
                1f
            ).apply {
                setMargins(DEFAULT_MARGIN)
            }
            setCornerRadius(5f.dp)
        }

        private fun createTextView() {
            textView = TextView(context).apply {
                setParams()
                text = key.toString()
                setTextColor(Color.BLACK)
                setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,
                    32f
                )
            }
            addView(textView)
        }

        private fun createIconView() {
            iconView = ImageView(context).apply {
                setParams()
            }
            addView(iconView)
        }

        fun setIcon(@DrawableRes res: Int?) {
            iconView?.let {
                val drawable: Drawable? = if (res == null) null else context.getCompatDrawable(res)
                if (isHaveIcon) it.updateImageViewImageAnimated(drawable) else it.setImageDrawable(drawable)
                setupClickListener()
            }
        }

        fun setupClickListener() {
            if (isHaveIcon || isHaveText == true) {
                setClickableAnimation()
                setOnClickListener {
                    makeVibrateFeedback()
                    if (isActionButton) checkActionClick() else onKeyClick()
                }
            } else {
                setOnClickListener(null)
                disableClickAnimation()
            }
        }

        private fun checkActionClick() {
            when (key) {
                RIGHT_ACTION_ID -> actionClickListener?.onRightActionClick()
                LEFT_ACTION_ID -> actionClickListener?.onLeftActionClick()
            }
        }

        private fun onKeyClick() {

        }

        private fun makeVibrateFeedback() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                performHapticFeedback(HapticFeedbackConstants.KEYBOARD_PRESS)
            } //todo
        }

        private fun disableClickAnimation() {
            foreground = null
        }

        private fun View.setParams() {
            layoutParams = LayoutParams(
                WRAP_CONTENT,
                WRAP_CONTENT,
                CENTER
            )
            setPadding(DEFAULT_PADDING)
        }
    }
}