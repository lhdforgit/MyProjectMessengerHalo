package com.hahalolo.pickercolor.util

import android.view.View

/**
 * View Extensions
 */

fun View.setVisibility(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}
