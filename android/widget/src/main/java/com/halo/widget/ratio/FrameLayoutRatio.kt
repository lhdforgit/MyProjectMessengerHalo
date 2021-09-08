/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.ratio

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.halo.widget.R


/**
 * @author admin
 * Created by admin
 * Created on 5/30/20.
 * Package com.halo.widget.ratio
 */
class FrameLayoutRatio : FrameLayout {

    var ratio: Float = 1F
        set(value) {
            field = value
            invalidate()
            requestLayout()
        }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    @SuppressLint("CustomViewStyleable")
    private fun init(context: Context, attrs: AttributeSet?) {
        attrs?.let {
            val a = context.obtainStyledAttributes(
                it,
                R.styleable.RatioGroup
            )
            with(a) {
                ratio = getFloat(
                    R.styleable.RatioGroup_ratio_group,
                    1F
                )
                recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width = measuredWidth
        var height = measuredHeight

        if (width == 0 && height == 0) {
            return
        }

        if (ratio > 0) {
            if (width > 0) {
                height = (width * ratio).toInt()
            } else {
                width = (height / ratio).toInt()
            }
        }

        setMeasuredDimension(width, height)
    }
}