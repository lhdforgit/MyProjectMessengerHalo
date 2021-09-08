/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.widget.materialdialogs.utils

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.JELLY_BEAN_MR1
import android.view.*
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Button
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.halo.widget.materialdialogs.MaterialDialog

/**
 * @author ndn
 * Create by ndn on 03/11/2018
 */

@Suppress("UNCHECKED_CAST")
internal fun <R : View> ViewGroup.inflate(
        ctxt: Context = context,
        @LayoutRes res: Int
) = LayoutInflater.from(ctxt).inflate(res, this, false) as R

@Suppress("UNCHECKED_CAST")
internal fun <T> MaterialDialog.inflate(
        @LayoutRes res: Int,
        root: ViewGroup? = null
) = LayoutInflater.from(windowContext).inflate(res, root, false) as T

internal fun <T : View> T.updatePadding(
        left: Int = this.paddingLeft,
        top: Int = this.paddingTop,
        right: Int = this.paddingRight,
        bottom: Int = this.paddingBottom
) {
    if (left == this.paddingLeft &&
            top == this.paddingTop &&
            right == this.paddingRight &&
            bottom == this.paddingBottom
    ) {
        // no change needed, don't want to invalidate layout
        return
    }
    this.setPadding(left, top, right, bottom)
}

internal fun <T : View> T.topMargin() = (this.layoutParams as MarginLayoutParams).topMargin

internal fun <T : View> T.updateMargin(
        left: Int = -1,
        top: Int = -1,
        right: Int = -1,
        bottom: Int = -1
) {
    val layoutParams = this.layoutParams as MarginLayoutParams
    if (left != -1) {
        layoutParams.leftMargin = left
    }
    if (top != -1) {
        layoutParams.topMargin = top
    }
    if (right != -1) {
        layoutParams.rightMargin = right
    }
    if (bottom != -1) {
        layoutParams.bottomMargin = bottom
    }
    this.layoutParams = layoutParams
}

internal inline fun <T : View> T.waitForLayout(crossinline f: T.() -> Unit) =
        viewTreeObserver.apply {
            addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    removeOnGlobalLayoutListener(this)
                    this@waitForLayout.f()
                }
            })
        }

internal fun <T : View> T.isVisible(): Boolean {
    return if (this is Button) {
        this.visibility == View.VISIBLE && this.text.trim().isNotBlank()
    } else {
        this.visibility == View.VISIBLE
    }
}

internal fun <T : View> T.isNotVisible(): Boolean {
    return !isVisible()
}

internal fun <T : View> T.isRtl(): Boolean {
    if (SDK_INT < JELLY_BEAN_MR1) return false
    return resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
}

internal fun TextView.setGravityStartCompat() {
    if (SDK_INT >= JELLY_BEAN_MR1) {
        this.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    } else {
        this.gravity = Gravity.START
    }
}

internal fun TextView.setGravityEndCompat() {
    if (SDK_INT >= JELLY_BEAN_MR1) {
        this.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
    } else {
        this.gravity = Gravity.END
    }
}
