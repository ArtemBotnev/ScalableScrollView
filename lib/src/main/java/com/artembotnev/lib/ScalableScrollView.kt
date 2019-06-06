package com.artembotnev.lib

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ScrollView
import android.widget.TextView

open class ScalableScrollView : ScrollView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attr: AttributeSet) : super(context, attr)

    private var scaleFactor = 1.0f
    private var defaultSize = 12.0f
    private var textView: TextView? = null

    private val textScaleListener by lazy {
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

            override fun onScale(detector: ScaleGestureDetector): Boolean {
                if (textView == null) return false

                defaultSize = textView!!.textSize

                scaleFactor *= detector.scaleFactor
                scaleFactor = Math.max(1.0f, Math.min(scaleFactor, MAX_ZOOM))

                textView!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultSize * scaleFactor)

                Log.d(TAG, scaleFactor.toString())

                return true
            }
        }
    }

    private val scaleDetector by lazy { ScaleGestureDetector(context, textScaleListener) }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (ev.pointerCount > 1) {
            val child = getChildAt(0)

            if (child is TextView) {
                return scaleText(child, ev)
            }
        }

        return super.onTouchEvent(ev)
    }

    private fun scaleText(textView: TextView, ev: MotionEvent): Boolean {
        this.textView = textView
        val handled = scaleDetector.onTouchEvent(ev)

        return if (handled) true else textView.onTouchEvent(ev)
    }

    companion object {
        private const val TAG = "ScalableScrollView"
        private const val MAX_ZOOM = 4.0f
    }
}