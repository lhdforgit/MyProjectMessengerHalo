/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.imageviewer.common.extensions

import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView

fun ImageView.copyBitmapFrom(target: ImageView?) {
    target?.drawable?.let {
        if (it is BitmapDrawable) {
            setImageBitmap(it.bitmap)
        }
    }
}