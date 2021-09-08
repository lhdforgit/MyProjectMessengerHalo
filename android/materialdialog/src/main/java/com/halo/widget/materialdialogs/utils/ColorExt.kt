/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.widget.materialdialogs.utils

import android.content.Context
import android.graphics.Color
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.halo.widget.materialdialogs.MaterialDialog

/**
 * @author ndn
 * Create by ndn on 03/11/2018
 */
@ColorInt
internal fun MaterialDialog.getColor(
        @ColorRes res: Int? = null,
        @AttrRes attr: Int? = null
): Int = getColor(windowContext, res, attr)

@ColorInt
internal fun getColor(
        context: Context,
        @ColorRes res: Int? = null,
        @AttrRes attr: Int? = null
): Int {
    if (attr != null) {
        val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
        try {
            return a.getColor(0, 0)
        } finally {
            a.recycle()
        }
    }
    return ContextCompat.getColor(context, res ?: 0)
}

internal fun Int.isColorDark(): Boolean {
    val darkness =
            1 - (0.299 * Color.red(this) + 0.587 * Color.green(this) + 0.114 * Color.blue(this)) / 255
    return darkness >= 0.5
}

internal fun TextView?.maybeSetTextColor(
        context: Context,
        @AttrRes attrRes: Int?
) {
    if (attrRes == null) return
    val color = getColor(context, attr = attrRes)
    if (color != 0) {
        this?.setTextColor(color)
    }
}
