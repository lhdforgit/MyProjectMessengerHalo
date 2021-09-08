/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.viewfliper

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import com.halo.widget.R

class HaloPagerIndicator : LinearLayout {

    lateinit var grouping: LinearLayout

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attributes: AttributeSet?) : super(context, attributes) {
        init(context, attributes)
    }

    private fun init(context: Context, attributes: AttributeSet?) {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.view_pager_indicator, this, true)
        grouping = findViewById(R.id.group)
    }

    fun changeColor(id: Int, @ColorInt dotColor: Int) {
        for (childId in 0 until grouping.childCount) {
            val dot = findViewById<DotView>(childId)
            if (id == childId) {
                dot.togleColor(dotColor, true)
            } else {
                dot.togleColor(dotColor, false)
            }
        }
    }

    fun setIndicator(size: Int) {
        grouping.removeAllViews()
        val width = 16 * resources.displayMetrics.density
        val heigh = 16 * resources.displayMetrics.density
        for (i in 0 until size) {
            val dot = DotView(context)
            dot.id = i
            dot.layoutParams = LayoutParams(width.toInt(), heigh.toInt())
            grouping.addView(dot)
        }
    }
}