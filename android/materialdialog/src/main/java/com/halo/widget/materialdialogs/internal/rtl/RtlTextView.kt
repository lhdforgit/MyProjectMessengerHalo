/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.widget.materialdialogs.internal.rtl

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.halo.widget.materialdialogs.utils.setGravityStartCompat

/**
 * With our custom layout-ing, using START/END gravity doesn't work so we manually
 * set text alignment for RTL/LTR.
 *
 * @author ndn
 * Create by ndn on 03/11/2018
 */
class RtlTextView(
        context: Context,
        attrs: AttributeSet?
) : AppCompatTextView(context, attrs) {

    init {
        setGravityStartCompat()
    }
}
