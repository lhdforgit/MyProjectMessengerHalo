/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.widget.materialdialogs.internal.main

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style.FILL
import android.graphics.Paint.Style.STROKE
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec.*
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import com.halo.widget.materialdialogs.MaterialDialog
import com.halo.widget.materialdialogs.R
import com.halo.widget.materialdialogs.internal.button.DialogActionButtonLayout
import com.halo.widget.materialdialogs.internal.title.DialogTitleLayout
import com.halo.widget.materialdialogs.utils.dimenPx

val DEBUG_COLOR_PINK = Color.parseColor("#EAA3CF")
val DEBUG_COLOR_DARK_PINK = Color.parseColor("#E066B1")
val DEBUG_COLOR_BLUE = Color.parseColor("#B5FAFB")

/**
 * The root layout of a dialog. Contains a [DialogTitleLayout], [DialogActionButtonLayout],
 * along with a content view that goes in between.
 *
 * @author ndn
 * Create by ndn on 03/11/2018
 */
internal class DialogLayout(
        context: Context,
        attrs: AttributeSet?
) : FrameLayout(context, attrs) {

    var maxHeight: Int = 0
    var debugMode: Boolean = false

    private val frameMarginVertical = dimenPx(R.dimen.md_dialog_frame_margin_vertical)
    private var debugPaint: Paint? = null

    internal val frameMarginVerticalLess = dimenPx(R.dimen.md_dialog_frame_margin_vertical_less)

    internal lateinit var dialog: MaterialDialog
    internal lateinit var titleLayout: DialogTitleLayout
    internal val contentView: View
        get() = getChildAt(1)
    internal lateinit var buttonsLayout: DialogActionButtonLayout

    init {
        setWillNotDraw(false)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        titleLayout = findViewById(R.id.md_title_layout)
        buttonsLayout = findViewById(R.id.md_button_layout)
    }

    internal fun invalidateDividers(
            scrolledDown: Boolean,
            atBottom: Boolean
    ) {
        titleLayout.drawDivider = scrolledDown
        buttonsLayout.drawDivider = atBottom
    }

    override fun onMeasure(
            widthMeasureSpec: Int,
            heightMeasureSpec: Int
    ) {
        val specWidth = getSize(widthMeasureSpec)
        var specHeight = getSize(heightMeasureSpec)
        if (specHeight > maxHeight) {
            specHeight = maxHeight
        }

        titleLayout.measure(
                makeMeasureSpec(specWidth, EXACTLY),
                makeMeasureSpec(0, UNSPECIFIED)
        )
        if (buttonsLayout.shouldBeVisible()) {
            buttonsLayout.measure(
                    makeMeasureSpec(specWidth, EXACTLY),
                    makeMeasureSpec(0, UNSPECIFIED)
            )
        }

        val titleAndButtonsHeight =
                titleLayout.measuredHeight + buttonsLayout.measuredHeight
        val remainingHeight = specHeight - titleAndButtonsHeight
        contentView.measure(
                makeMeasureSpec(specWidth, EXACTLY),
                makeMeasureSpec(remainingHeight, AT_MOST)
        )

        val totalHeight = titleLayout.measuredHeight +
                contentView.measuredHeight +
                buttonsLayout.measuredHeight
        setMeasuredDimension(specWidth, totalHeight)
    }

    override fun onLayout(
            changed: Boolean,
            left: Int,
            top: Int,
            right: Int,
            bottom: Int
    ) {
        val titleLeft = 0
        val titleTop = 0
        val titleRight = measuredWidth
        val titleBottom = titleLayout.measuredHeight
        titleLayout.layout(
                titleLeft,
                titleTop,
                titleRight,
                titleBottom
        )

        val buttonsTop =
                measuredHeight - buttonsLayout.measuredHeight
        if (buttonsLayout.shouldBeVisible()) {
            val buttonsLeft = 0
            val buttonsRight = measuredWidth
            val buttonsBottom = measuredHeight
            buttonsLayout.layout(
                    buttonsLeft,
                    buttonsTop,
                    buttonsRight,
                    buttonsBottom
            )
        }

        val contentLeft = 0
        val contentRight = measuredWidth
        contentView.layout(
                contentLeft,
                titleBottom,
                contentRight,
                buttonsTop
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (debugMode) {
            val contentPadding =
                    if (titleLayout.shouldNotBeVisible()) frameMarginVerticalLess else frameMarginVertical
            if (titleLayout.shouldNotBeVisible()) {
                // Content top spacing
                canvas.drawRect(
                        0f,
                        titleLayout.bottom.toFloat(),
                        measuredWidth.toFloat(),
                        titleLayout.bottom.toFloat() + contentPadding,
                        debugPaint(DEBUG_COLOR_BLUE)
                )
            }
            // Content bottom spacing
            canvas.drawRect(
                    0f,
                    buttonsLayout.top.toFloat() - contentPadding,
                    measuredWidth.toFloat(),
                    buttonsLayout.top.toFloat(),
                    debugPaint(DEBUG_COLOR_BLUE)
            )
        }
    }

    fun debugPaint(
            @ColorInt color: Int,
            stroke: Boolean = false
    ): Paint {
        if (debugPaint == null) {
            debugPaint = Paint()
        }
        debugPaint!!.style = if (stroke) STROKE else FILL
        debugPaint!!.color = color
        return debugPaint!!
    }
}
