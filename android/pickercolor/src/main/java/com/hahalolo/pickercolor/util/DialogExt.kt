package com.hahalolo.pickercolor.util

import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.hahalolo.pickercolor.R

/**
 * AlertDialog Extensions
 */

/**
 * Set AlertDialog Button Text Color
 */
fun AlertDialog.setButtonTextColor() {
    val positiveTextColor = ContextCompat.getColor(context, R.color.positiveButtonTextColor)
    getButton(DialogInterface.BUTTON_POSITIVE)?.setTextColor(positiveTextColor)

    val negativeTextColor = ContextCompat.getColor(context, R.color.negativeButtonTextColor)
    getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(negativeTextColor)
}
