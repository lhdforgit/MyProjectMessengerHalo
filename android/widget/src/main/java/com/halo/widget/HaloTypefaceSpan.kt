/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.TypefaceSpan
import androidx.core.content.res.ResourcesCompat

/**
 * @author ndn
 * Created by ndn
 * Created on 9/24/18
 */
@SuppressLint("ParcelCreator")
class HaloTypefaceSpan : TypefaceSpan {

    private val newType: Typeface?

    constructor(family: String?, type: Typeface?) : super(family) {
        newType = type
    }

    override fun updateDrawState(ds: TextPaint) {
        applyCustomTypeFace(ds, newType)
    }

    override fun updateMeasureState(paint: TextPaint) {
        applyCustomTypeFace(paint, newType)
    }

    private fun applyCustomTypeFace(paint: Paint, tf: Typeface?) {
        try {
            val oldStyle: Int
            val old = paint.typeface
            oldStyle = old?.style ?: 0
            val fake = oldStyle and tf!!.style.inv()
            if (fake and Typeface.BOLD != 0) {
                paint.isFakeBoldText = true
            }
            if (fake and Typeface.ITALIC != 0) {
                paint.textSkewX = -0.25f
            }
            paint.typeface = tf
            paint.flags = paint.flags or Paint.SUBPIXEL_TEXT_FLAG
        } catch (e: Exception) {
        }
    }

    companion object {
        @JvmStatic
        fun REGULAR(context: Context) = HaloTypefaceSpan(
            null,
            ResourcesCompat.getFont(context, R.font.muli_regular)
        )
        @JvmStatic
        fun MEDIUM(context: Context) = HaloTypefaceSpan(
            null,
            ResourcesCompat.getFont(context, R.font.muli_medium)
        )
        @JvmStatic
        fun SEMI_BOLD(context: Context) = HaloTypefaceSpan(
            null,
            ResourcesCompat.getFont(context, R.font.muli_semibold)
        )
        @JvmStatic
        fun BOLD(context: Context) = HaloTypefaceSpan(
            null,
            ResourcesCompat.getFont(context, R.font.muli_bold)
        )
        @JvmStatic
        fun EXTRA_BOLD(context: Context) = HaloTypefaceSpan(
            null,
            ResourcesCompat.getFont(context, R.font.muli_extrabold)
        )
    }
}