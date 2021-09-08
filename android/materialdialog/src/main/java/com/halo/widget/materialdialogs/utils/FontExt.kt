/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.widget.materialdialogs.utils

import android.graphics.Typeface
import androidx.annotation.AttrRes
import androidx.annotation.CheckResult
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import com.halo.widget.materialdialogs.MaterialDialog
import com.halo.widget.materialdialogs.assertOneSet

/**
 * @author ndn
 * Create by ndn on 03/11/2018
 */
@CheckResult
internal fun MaterialDialog.font(
        @FontRes res: Int? = null,
        @AttrRes attr: Int? = null
): Typeface? {
    assertOneSet("font", attr, res)
    if (res != null) {
        return ResourcesCompat.getFont(windowContext, res)
    }

    val a = windowContext.theme.obtainStyledAttributes(intArrayOf(attr!!))
    try {
        val resId = a.getResourceId(0, 0)
        if (resId == 0) return null
        return ResourcesCompat.getFont(windowContext, resId)
    } finally {
        a.recycle()
    }
}
