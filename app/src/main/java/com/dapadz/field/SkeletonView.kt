package com.dapadz.field

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

class SkeletonView @JvmOverloads constructor(
    private val context: Context,
    private val attributeSet: AttributeSet? = null
) : View(context, attributeSet) {

    private val TAG = "SkeletonView"

    private val refreshIntervalInMillis = ((1000f / context.refreshRateInSeconds()) * .9f).toLong()
    private val matrix: Matrix = Matrix()
    private val angle = 45.0
    private val durationInMillis = 2800L

    private val skeletonColor = Color.LTGRAY
    private val shimmerColor = Color.DKGRAY

    private val gradient get() = createGradient()

    private var animationTask: Runnable? = null
    private var animation: Handler? = null

    private var rootWidth = 0f
    private var rootHeight = 0f

    private var rectF = RectF()
    private val paint = Paint().apply { isAntiAlias = true }

    var radius = 10f.dp
        set(value) {
            field = value
            invalidate()
        }

    private fun startAnimation() {
        if (animation == null) {
            animation = Handler(Looper.getMainLooper())
            animationTask = object : Runnable {
                override fun run() {
                    updateShimmer()
                    animation?.postDelayed(this, refreshIntervalInMillis)
                }
            }
            animationTask?.let { task -> animation?.post(task) }
        }
    }

    private fun stopAnimation() {
        animationTask?.let { task -> animation?.removeCallbacks(task) }
        animation = null
    }

//    private fun findLastParent(view: View): View {
//        val parent = view.parent
//        return if (parent is View) {
//            findLastParent(parent)
//        } else {
//            view
//        }
//    }



    private fun updateShimmer() {
        try {
            matrix.setTranslate(-currentOffset(), 0f)
            paint.shader.setLocalMatrix(matrix)
            invalidate()
        }catch (t:Throwable) {
        }
    }

//    private fun currentOffset(): Float {
//        val progress = currentProgress()
//        val offset = rootWidth * 2
//        val min = -offset
//        val max = rootWidth + offset
//        return progress * (max - min) + min
//    }

//    private fun currentOffset(): Float {
//        val progress = currentProgress()
//        val offset = rootWidth * 2
//        val min = -offset
//        val max = rootWidth + offset
//
//        val viewLocation = IntArray(2)
//        getLocationOnScreen(viewLocation)
//        val viewX = viewLocation[0].toFloat()
//        val viewWidth = measuredWidth.toFloat()
//
//        return viewX + viewWidth - progress * (max - min) - min
//    }


    private fun currentOffset(): Float {
        val progress = currentProgress()
        val offset = rootWidth * 2
        val min = -offset
        val max = rootWidth + offset + (rootWidth - measuredWidth) / 2f

        val viewLocation = IntArray(2)
        getLocationOnScreen(viewLocation)
        val viewX = viewLocation[0].toFloat()
        val viewWidth = measuredWidth.toFloat()

        val offsetValue = viewX + viewWidth - progress * (max - min) - min

        return if (progress >= 1f) {
            viewX + viewWidth - max  // Задаем фиксированное значение для конца анимации
        } else {
            offsetValue
        }
    }

    private fun currentProgress(): Float {
        val millis = System.currentTimeMillis()
        val current = millis.toDouble()
        val interval = durationInMillis
        val divisor = floor(current / interval)
        val start = interval * divisor
        val end = start + interval
        val percentage = (current - start) / (end - start)
        return percentage.toFloat()
    }

    private fun updateRectPosition(){
        val left = 0f
        val top = 0f
        val right = measuredWidth.toFloat()
        val bottom = measuredHeight.toFloat()
        rectF.set(left, top, right, bottom)
    }

    private fun createGradient(): LinearGradient {
        val radians = Math.toRadians(angle).toFloat()
        val startX = cos(radians) * rootWidth
        val startY = sin(radians) * rootHeight
        val endX = -startX
        val endY = -startY
        return LinearGradient(
            startX, startY, endX, endY,
            intArrayOf(skeletonColor, shimmerColor, skeletonColor),
            null,
            Shader.TileMode.CLAMP
        )
    }

    private fun Context.refreshRateInSeconds(): Float {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        return windowManager?.defaultDisplay?.refreshRate ?: 60f
    }

    private fun measureDimension(desiredSize: Int, measureSpec: Int): Int {
        var result: Int
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = desiredSize
            if (specMode == MeasureSpec.AT_MOST) {
                result = result.coerceAtMost(specSize)
            }
        }
        return result
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        updateRectPosition()
        canvas?.drawRoundRect(rectF, radius, radius, paint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
//        rootWidth = findLastParent(this).measuredWidth.toFloat()
//        rootHeight = findLastParent(this).measuredHeight.toFloat()
        rootWidth = (rootView as ViewGroup).measuredWidth.toFloat()
        rootHeight = (rootView as ViewGroup).measuredHeight.toFloat()
//        Log.e(TAG, findLastParent(this).toString())
//        rootWidth = (parent as ViewGroup).measuredWidth.toFloat()
//        rootHeight = (parent as ViewGroup).measuredHeight.toFloat()
        paint.shader = gradient
        invalidate()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val desiredWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val desiredHeight = suggestedMinimumHeight + paddingTop + paddingBottom
        setMeasuredDimension(
            measureDimension(desiredWidth, widthMeasureSpec),
            measureDimension(desiredHeight, heightMeasureSpec)
        )
    }
}