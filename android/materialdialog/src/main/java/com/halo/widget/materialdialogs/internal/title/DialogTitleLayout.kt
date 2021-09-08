/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.widget.materialdialogs.internal.title

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View.MeasureSpec.*
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import com.halo.widget.materialdialogs.R
import com.halo.widget.materialdialogs.internal.main.BaseSubLayout
import com.halo.widget.materialdialogs.internal.main.DEBUG_COLOR_DARK_PINK
import com.halo.widget.materialdialogs.internal.main.DEBUG_COLOR_PINK
import com.halo.widget.materialdialogs.utils.dimenPx
import com.halo.widget.materialdialogs.utils.isNotVisible
import com.halo.widget.materialdialogs.utils.isRtl
import com.halo.widget.materialdialogs.utils.isVisible
import java.lang.Math.max

/**
 * Manages the header frame of the dialog, including the optional icon and title.
 *
 * @author ndn
 * Create by ndn on 03/11/2018
 */
internal class DialogTitleLayout(
        context: Context,
        attrs: AttributeSet? = null
) : BaseSubLayout(context, attrs) {

    private val frameMarginVertical = dimenPx(R.dimen.md_dialog_frame_margin_vertical)
    private val titleMarginBottom = dimenPx(R.dimen.md_dialog_title_layout_margin_bottom)
    private val frameMarginHorizontal = dimenPx(R.dimen.md_dialog_frame_margin_horizontal)

    private val iconMargin = dimenPx(R.dimen.md_icon_margin)
    private val iconSize = dimenPx(R.dimen.md_icon_size)

    internal lateinit var iconView: ImageView
    internal lateinit var titleView: AppCompatTextView

    override fun onFinishInflate() {
        super.onFinishInflate()
        iconView = findViewById(R.id.md_icon_title)
        titleView = findViewById(R.id.md_text_title)
    }

    fun shouldNotBeVisible() =
            iconView.isNotVisible() && titleView.isNotVisible()

    override fun onMeasure(
            widthMeasureSpec: Int,
            heightMeasureSpec: Int
    ) {
        if (shouldNotBeVisible()) {
            setMeasuredDimension(0, 0)
            return
        }

        val parentWidth = getSize(widthMeasureSpec)
        var titleMaxWidth =
                parentWidth - (frameMarginHorizontal * 2)

        if (iconView.isVisible()) {
            iconView.measure(
                    makeMeasureSpec(iconSize, EXACTLY),
                    makeMeasureSpec(iconSize, EXACTLY)
            )
            titleMaxWidth -= (iconView.measuredWidth + iconMargin)
        }

        titleView.measure(
                makeMeasureSpec(titleMaxWidth, AT_MOST),
                makeMeasureSpec(0, UNSPECIFIED)
        )

        val iconViewHeight =
                if (iconView.isVisible()) iconView.measuredHeight else 0
        val requiredHeight = max(
                iconViewHeight, titleView.measuredHeight
        )
        val actualHeight = requiredHeight + frameMarginVertical + titleMarginBottom

        setMeasuredDimension(
                parentWidth,
                actualHeight
        )
    }

    override fun onLayout(
            changed: Boolean,
            left: Int,
            top: Int,
            right: Int,
            bottom: Int
    ) {
        if (shouldNotBeVisible()) return

        var titleLeft: Int
        val titleBottom: Int
        val titleTop: Int
        var titleRight: Int

        if (isRtl()) {
            titleRight = measuredWidth - frameMarginHorizontal
            titleBottom = measuredHeight - titleMarginBottom
            titleTop = titleBottom - titleView.measuredHeight
            titleLeft = titleRight - titleView.measuredWidth
        } else {
            titleLeft = frameMarginHorizontal
            titleBottom = measuredHeight - titleMarginBottom
            titleTop = titleBottom - titleView.measuredHeight
            titleRight = titleLeft + titleView.measuredWidth
        }

        if (iconView.isVisible()) {
            val titleHalfHeight = (titleBottom - titleTop) / 2
            val titleMidPoint = titleBottom - titleHalfHeight
            val iconHalfHeight = iconView.measuredHeight / 2

            val iconLeft: Int
            val iconTop: Int
            val iconRight: Int
            val iconBottom: Int

            if (isRtl()) {
                iconRight = titleRight
                iconTop = titleMidPoint - iconHalfHeight
                iconLeft = iconRight - iconView.measuredWidth
                iconBottom = iconTop + iconView.measuredHeight
                titleRight = iconLeft - iconMargin
                titleLeft = titleRight - titleView.measuredWidth
            } else {
                iconLeft = titleLeft
                iconTop = titleMidPoint - iconHalfHeight
                iconRight = iconLeft + iconView.measuredWidth
                iconBottom = iconTop + iconView.measuredHeight
                titleLeft = iconRight + iconMargin
                titleRight = titleLeft + titleView.measuredWidth
            }
            iconView.layout(iconLeft, iconTop, iconRight, iconBottom)
        }

        titleView.layout(titleLeft, titleTop, titleRight, titleBottom)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (dialogParent().debugMode) {
            // Fill above the title
            canvas.drawRect(
                    0f,
                    0f,
                    measuredWidth.toFloat(),
                    frameMarginVertical.toFloat(),
                    debugPaint(DEBUG_COLOR_PINK)
            )
            // Fill below the title
            canvas.drawRect(
                    0f,
                    measuredHeight.toFloat() - titleMarginBottom,
                    measuredWidth.toFloat(),
                    measuredHeight.toFloat(),
                    debugPaint(DEBUG_COLOR_PINK)
            )
            // Fill to the left of the title
            canvas.drawRect(
                    0f,
                    0f,
                    frameMarginHorizontal.toFloat(),
                    measuredHeight.toFloat(),
                    debugPaint(DEBUG_COLOR_DARK_PINK)
            )
            // Fill to the right of the title
            canvas.drawRect(
                    measuredWidth.toFloat() - frameMarginHorizontal,
                    0f,
                    measuredWidth.toFloat(),
                    measuredHeight.toFloat(),
                    debugPaint(DEBUG_COLOR_DARK_PINK)
            )
        }

        if (drawDivider) {
            canvas.drawLine(
                    0f,
                    measuredHeight.toFloat() - dividerHeight.toFloat(),
                    measuredWidth.toFloat(),
                    measuredHeight.toFloat(),
                    dividerPaint()
            )
        }
    }
}
