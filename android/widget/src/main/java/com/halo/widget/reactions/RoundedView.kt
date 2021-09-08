/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.reactions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.View


/**
 *
 * @author ndn
 * Created by ndn
 * Created on 5/30/19.
 *
 * Reaction selector floating dialog background.
 */
@SuppressLint("ViewConstructor")
class RoundedView(context: Context, private val config: ReactionsConfig) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = config.popupColor
        style = Paint.Style.FILL
        alpha = 255
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#DDDCDC")
        style = Paint.Style.FILL
        alpha = 120
    }

    private var cornerSize = 0f

    private var xStart = 0f
    private var yStart = 0f

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)

        val yPad = if (paddingTop + paddingBottom <= 0) {
            config.verticalMargin * 2f
        } else {
            (paddingTop + paddingBottom) / 2f
        }
        val xPad = if (paddingLeft + paddingRight <= 0) {
            config.horizontalMargin * 2f
        } else {
            (paddingLeft + paddingRight) / 2f
        }
        val bPad = xPad / 2
        val nIcons = config.reactions.size
        val regIconSize = (w - (2 * xPad + (nIcons - 1) * bPad)) / nIcons
        cornerSize = xPad + regIconSize / 2 + yPad
        xStart = cornerSize
        yStart = 0f
    }

    private val path = Path()
    private val rect = RectF()

    private val borderPath = Path()
    private val borderRect = RectF()

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Draw the background rounded rectangle
        borderPath.moveTo(xStart, yStart)

        // Top line between curves
        borderPath.lineTo(xStart + (width - 2 * cornerSize), yStart)

        // First curve, right side
        rect.left = xStart + width - 2 * cornerSize
        rect.right = rect.left + cornerSize - 3
        rect.top = yStart + 3
        rect.bottom = yStart + height - 3
        path.arcTo(rect, 270f, 180f)

        borderRect.left = xStart + width - 2 * cornerSize
        borderRect.right = rect.left + cornerSize
        borderRect.top = yStart
        borderRect.bottom = yStart + height
        borderPath.arcTo(borderRect, 270f, 180f)

        // Bottom line between curves
        borderPath.lineTo(xStart, yStart + height)

        // Second curve, left side
        rect.left = xStart - cornerSize + 3
        rect.right = xStart
        rect.top = yStart + 3
        rect.bottom = yStart + height - 3
        path.arcTo(rect, 90f, 180f)

        borderRect.left = xStart - cornerSize
        borderRect.right = xStart
        borderRect.top = yStart
        borderRect.bottom = yStart + height
        borderPath.arcTo(borderRect, 90f, 180f)

        borderPath.close()
        canvas.drawPath(borderPath, borderPaint)
        borderPath.reset()

        path.close()
        canvas.drawPath(path, paint)
        path.reset()
    }
}
