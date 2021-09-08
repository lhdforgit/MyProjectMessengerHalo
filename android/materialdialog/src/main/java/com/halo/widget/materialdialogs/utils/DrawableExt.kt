/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.widget.materialdialogs.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

/**
 * @author ndn
 * Create by ndn on 03/11/2018
 */
internal fun getDrawable(
        context: Context,
        @DrawableRes res: Int? = null,
        @AttrRes attr: Int? = null,
        fallback: Drawable? = null
): Drawable? {
    if (attr != null) {
        val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
        try {
            var d = a.getDrawable(0)
            if (d == null && fallback != null) {
                d = fallback
            }
            return d
        } finally {
            a.recycle()
        }
    }
    if (res == null) return fallback
    return ContextCompat.getDrawable(context, res)
}
