package com.artembotnev.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ViewTreeObserver
import android.widget.ScrollView
import android.widget.TextView

import kotlin.math.max
import kotlin.math.min

open class ScalableScrollView : ScrollView {

    private var scaleFactor = 1.0f
    private var startTextSize = 12.0f
    private var maxScale = MAX_SCALE
    private var textView: TextView? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        context.theme.obtainStyledAttributes(
            attrs, R.styleable.ScalableScrollView, 0, 0
        ).run {
            maxScale = getFloat(R.styleable.ScalableScrollView_maxScale, MAX_SCALE).also {
                min(it, MAX_SCALE)
            }
        }
    }

    init {
        val preDrawListener = object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                viewTreeObserver.removeOnPreDrawListener(this)
                adjustTextView()

                return true
            }
        }

        viewTreeObserver.addOnPreDrawListener(preDrawListener)
    }

    private val textScaleListener by lazy {
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

            override fun onScale(detector: ScaleGestureDetector): Boolean {
                if (textView == null) return false

                scaleFactor *= detector.scaleFactor
                scaleFactor = max(1.0f, min(scaleFactor, maxScale))

                textView!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, startTextSize * scaleFactor)

                Log.d(TAG, "Scale factor: $scaleFactor")

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

        return if (handled) true else textView!!.onTouchEvent(ev)
    }

    private fun adjustTextView() = getChildAt(0).let {
            if (it is TextView) {
                textView = it
                startTextSize = it.textSize
            }
        }

    companion object {
        private const val TAG = "ScalableScrollView"
        private const val MAX_SCALE = 15.0f
    }
}