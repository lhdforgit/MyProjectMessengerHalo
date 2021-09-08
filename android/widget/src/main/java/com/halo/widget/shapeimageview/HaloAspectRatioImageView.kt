/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.shapeimageview

/**
 *   @author ngannd
 *   Create by ngannd on 18/01/2019
 *
 *   <com.halo.widget.shapeimageview.HaloAspectRatioImageView
 *      android:layout_width="200dp"
 *      android:layout_height="0dp"
 *      android:scaleType="centerCrop"
 *      android:src="@drawable/sample"
 *      app:ari_ratio="1.2"/>
 */

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.halo.widget.R

class HaloAspectRatioImageView : AppCompatImageView {

    var ratio: Float =
        DEFAULT_RATIO
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

    private fun init(context: Context, attrs: AttributeSet?) {
        attrs?.let {
            val a = context.obtainStyledAttributes(it,
                R.styleable.HaloAspectRatioImageView
            )
            with(a) {
                ratio = getFloat(
                    R.styleable.HaloAspectRatioImageView_ari_ratio,
                    DEFAULT_RATIO
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

    companion object {
        const val DEFAULT_RATIO = 1F
    }
}