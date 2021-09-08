/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget

import android.content.Context
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

/**
 * Create by ndn
 * Create on 7/11/20
 * com.halo.widget
 */
abstract class HahaloloClickableSpan(val context: Context) : ClickableSpan() {

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.color = ContextCompat.getColor(context, R.color.text_body)
        ds.isUnderlineText = false
        ds.typeface = ResourcesCompat.getFont(context, R.font.muli_bold)
    }

    companion object {
        @JvmStatic
        fun REGULAR(context: Context, click: () -> Unit) =
            object : HahaloloClickableSpan(context) {
                override fun onClick(widget: View) {
                    click.invoke()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = ContextCompat.getColor(context, R.color.text_body)
                    ds.isUnderlineText = false
                    ds.typeface = ResourcesCompat.getFont(context, R.font.muli_regular)
                }
            }
        @JvmStatic
        fun BOLD(context: Context, click: () -> Unit) =
            object : HahaloloClickableSpan(context) {
                override fun onClick(widget: View) {
                    click.invoke()
                }
            }

        @JvmStatic
        fun PRIMARY(context: Context, click: () -> Unit) =
            object : HahaloloClickableSpan(context) {
                override fun onClick(widget: View) {
                    click.invoke()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = ContextCompat.getColor(context, R.color.text_primary)
                    ds.isUnderlineText = false
                    ds.typeface = ResourcesCompat.getFont(context, R.font.muli_regular)
                }
            }

        @JvmStatic
        fun BOLD_NOTICE(context: Context, click: () -> Unit) =
            object : HahaloloClickableSpan(context) {
                override fun onClick(widget: View) {
                    click.invoke()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = ContextCompat.getColor(context, R.color.text_notice)
                    ds.isUnderlineText = false
                    ds.typeface = ResourcesCompat.getFont(context, R.font.muli_bold)
                }
            }
    }
}