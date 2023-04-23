package com.dapadz.field

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Outline
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.abs


val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Float.dp: Float
    get() = (this * Resources.getSystem().displayMetrics.density)


fun View.setClickableAnimation() {
    val outValue = TypedValue()
    context.theme.resolveAttribute(
        android.R.attr.selectableItemBackground, outValue, true
    )
    foreground = context.getCompatDrawable(outValue.resourceId)
}

fun View?.shake() {
    if (this == null || !isAttachedToWindow) return
    val shakeDuration = 200L
    val shakeDistance = 2f.dp
    val initialTranslationX = translationX
    ValueAnimator.ofFloat(0f, shakeDistance, 0f, -shakeDistance, 0f).apply {
        duration = shakeDuration
        interpolator = FastOutSlowInInterpolator()
        addUpdateListener {
            val newTranslationX = initialTranslationX + it.animatedValue as Float
            translationX = newTranslationX
        }
        doOnEnd { translationY = initialTranslationX }
    }.start()
}

fun View.setCornerRadius(radius: Float) {
    outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            val isAutoResizeEnabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P //sdk 28, android 9
            val outlineRadius = if (!isAutoResizeEnabled) radius / 2 else radius
            outline.setRoundRect(
                0,0, view.width, view.height, outlineRadius
            )
        }
    }
    clipToOutline = true
}

fun ImageView.updateImageViewImageAnimated(@DrawableRes newIcon : Int) {
    val drawable = ContextCompat.getDrawable(context, newIcon) ?: return
    updateImageViewImageAnimated(drawable)
}

fun ImageView.updateImageViewImageAnimated(newIcon: Drawable) {
    if (drawable == newIcon) {
        return
    }
    ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 150
        val changed = AtomicBoolean()
        addUpdateListener {
            val animValue = it.animatedValue as Float
            val scale = 0.5f + abs(animValue - 0.5f)
            scaleX = scale
            scaleY = scale
            if (animValue >= 0.5f && ! changed.get()) {
                changed.set(true)
                setImageDrawable(newIcon)
            }
        }
    }.start()
}

fun Context.getCompatColor(@ColorRes color: Int) = ContextCompat.getColor(this, color)
fun Context.getCompatDrawable(@DrawableRes drawable: Int) = ContextCompat.getDrawable(this, drawable)