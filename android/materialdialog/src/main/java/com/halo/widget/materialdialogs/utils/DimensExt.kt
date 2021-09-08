/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.widget.materialdialogs.utils

import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.DimenRes
import com.halo.widget.materialdialogs.MaterialDialog
import com.halo.widget.materialdialogs.R
import com.halo.widget.materialdialogs.assertOneSet

/**
 * @author ndn
 * Create by ndn on 03/11/2018
 */
internal fun MaterialDialog.dimen(
        @DimenRes res: Int? = null,
        @AttrRes attr: Int? = null,
        fallback: Float = windowContext.resources.getDimension(R.dimen.md_dialog_default_corner_radius)
): Float {
    assertOneSet("dimen", attr, res)
    if (res != null) {
        return windowContext.resources.getDimension(res)
    }
    val a = windowContext.theme.obtainStyledAttributes(intArrayOf(attr!!))
    try {
        return a.getDimension(0, fallback)
    } finally {
        a.recycle()
    }
}

internal fun <T : View> T.dimenPx(@DimenRes res: Int): Int {
    return context.resources.getDimensionPixelSize(res)
}
