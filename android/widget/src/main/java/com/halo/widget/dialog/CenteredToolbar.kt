/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.dialog

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.halo.widget.R


/**
 * Customized the `RelativeLayout` inside the `Toolbar`.
 * Because by default the custom view in the `Toolbar` cannot set
 * width as our expect value. So this class allows setting the width of the
 * `RelativeLayout` to take up any percentage of the screen width.
 */
class CenteredToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.toolbarStyle
) : Toolbar(context, attrs, defStyleAttr) {

    private val titleView: TextView? = null
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val childCount = childCount
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            if (view is RelativeLayout) {
                val width = measuredWidth
                val layoutParams = view.getLayoutParams()
                layoutParams.width = (width * WIDTH_PERCENTAGE).toInt()
                view.setLayoutParams(layoutParams)
                break
            }
        }
    }

    override fun onLayout(
        changed: Boolean,
        l: Int,
        t: Int,
        r: Int,
        b: Int
    ) {
        super.onLayout(changed, l, t, r, b)
        val childCount = childCount
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            if (view is RelativeLayout) {
                forceTitleCenter(view)
                break
            }
        }
    }

    /**
     * Centering the layout.
     *
     * @param view The view to be centered
     */
    private fun forceTitleCenter(view: View) {
        val toolbarWidth = measuredWidth
        val relativeLayoutWidth = view.measuredWidth
        val newLeft = (toolbarWidth - relativeLayoutWidth) / 2
        val top = view.top
        val newRight = newLeft + relativeLayoutWidth
        val bottom = view.bottom
        view.layout(newLeft, top, newRight, bottom)
    }

    companion object {
        private const val WIDTH_PERCENTAGE = 0.8
    }
}