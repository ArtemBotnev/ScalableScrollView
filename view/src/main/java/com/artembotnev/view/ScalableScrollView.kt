package com.artembotnev.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ScrollView
import android.widget.TextView

open class ScalableScrollView : ScrollView {

    private var scaleFactor = 1.0f
    private var maxScale = MAX_SCALE
    private var textView: TextView? = null

    constructor(context: Context) : super(context) {
        adjustTextView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        context.theme.obtainStyledAttributes(
            attrs, R.styleable.ScalableScrollView, 0, 0
        ).run {
            maxScale = getFloat(R.styleable.ScalableScrollView_maxScale, MAX_SCALE).also {
                Math.min(it, MAX_SCALE)
            }
        }

        adjustTextView()
    }

    private val textScaleListener by lazy {
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

            override fun onScale(detector: ScaleGestureDetector): Boolean {
                if (textView == null) return false

                val textSize = textView!!.textSize

                scaleFactor *= detector.scaleFactor
                scaleFactor = Math.max(1.0f, Math.min(scaleFactor, maxScale))

                textView!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize * scaleFactor)

                Log.d(TAG, scaleFactor.toString())

                return true
            }
        }
    }

    private val scaleDetector by lazy { ScaleGestureDetector(context, textScaleListener) }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent) =
        if (ev.pointerCount > 1) scaleText(ev) else super.onTouchEvent(ev)

    private fun scaleText(ev: MotionEvent): Boolean {
        if (textView == null) return false

        val handled = scaleDetector.onTouchEvent(ev)

        return if (handled) {
            textView!!.invalidate()
            true
        } else {
            textView!!.onTouchEvent(ev)
        }
    }

    private fun adjustTextView() =
        getChildAt(0).let { if (it is TextView) textView = it }

    companion object {
        private const val TAG = "ScalableScrollView"
        private const val MAX_SCALE = 10.0f
    }
}