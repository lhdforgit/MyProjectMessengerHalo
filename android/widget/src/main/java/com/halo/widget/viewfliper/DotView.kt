/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.viewfliper

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class DotView : View {

    private val density by lazy {
        resources.displayMetrics.density
    }

    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var mRadius: Float = 0f
    private val dotPaint by lazy {
        return@lazy Paint(Paint.ANTI_ALIAS_FLAG)
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attributes: AttributeSet?) : super(context, attributes) {
        init(context, attributes)
    }

    constructor(context: Context, attributes: AttributeSet?, desRes: Int) : super(context, attributes, desRes) {
        init(context, attributes)
    }

    fun init(context: Context, attributes: AttributeSet?) {
        dotPaint.color = Color.GRAY
        dotPaint.style = Paint.Style.FILL
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mWidth = (width * density).toInt()
        mHeight = (height * density).toInt()
        mRadius = getRadiusDot()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        drawDot(canvas)
        super.onDraw(canvas)
    }

    fun drawDot(canvas: Canvas?) {
        if (canvas != null) {
            canvas.drawCircle(width / 2.toFloat(), (height / 2).toFloat(), getRadiusDot(), dotPaint)
        }
    }


    fun togleColor(int: Int, isTogle: Boolean) {
        if (isTogle) {
            dotPaint.color = int
        } else {
            dotPaint.color = Color.GRAY
        }
        invalidate()
    }


    fun startIn(scale: Float) {
        scaleX = scale
        scaleY = scale
        invalidate()
    }

    fun getRadiusDot(): Float {
        return (width - OFFSET) / 4
    }


    fun startOut() {

    }

    companion object {
        const val OFFSET: Float = 10f
    }
}