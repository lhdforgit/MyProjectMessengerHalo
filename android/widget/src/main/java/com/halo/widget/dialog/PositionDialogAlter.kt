package com.halo.widget.dialog

import android.app.Activity
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.ColorRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.halo.common.utils.SizeUtils
import com.halo.common.utils.log
import com.halo.widget.R
import kotlinx.android.synthetic.main.position_dialog_alter.*

class PositionDialogAlter {
    /***
     * Positions For Alert Dialog
     * */
    enum class POSITIONS {
        CENTER, BOTTOM
    }

    companion object {

        private lateinit var layoutInflater: LayoutInflater

        /***
         * core method For Alert Dialog
         * */
        fun build(
            context: Activity,
            orientation: Int = LinearLayout.VERTICAL,
            cancelable: Boolean = true
        ): AlertDialog {
            layoutInflater = LayoutInflater.from(context)
            val alertDialog =
                AlertDialog.Builder(
                    context, R.style.full_screen_dialog
                ).apply {
                    setCancelable(true)
                    setView(R.layout.position_dialog_alter)
                }

            val alert: AlertDialog = alertDialog.create()
            // Let's start with animation work. We just need to create a style and use it here as follows.
            //Pop In and Pop Out Animation yet pending
            //alert.window?.attributes?.windowAnimations = R.style.SlidingDialogAnimation
            alert.show()
            alert.btnOrientation(orientation)
            if (cancelable) {
                runCatching {
                    // prevent click inside layout of content dialog dissmiss
                    alert.mainLayout.setOnClickListener {

                    }
                    alert.wrapper.setOnClickListener {
                        alert.dismiss()
                    }
                }.getOrElse {
                    alert.cancel()
                }
            }
            return alert
        }
    }
}

private fun AlertDialog.btnOrientation(orientation: Int) {
    this.btnGroup.orientation = orientation
    if (orientation == LinearLayout.HORIZONTAL) {
        this.yesButton?.layoutParams = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            weight = 1f
            rightMargin = SizeUtils.dp2px(8f)
        }
        this.noButton?.layoutParams = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            weight = 1f
            rightMargin = 0
        }
    } else {
        this.yesButton?.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            rightMargin = SizeUtils.dp2px(32f)
            leftMargin = SizeUtils.dp2px(32f)
        }
        this.noButton?.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            rightMargin = SizeUtils.dp2px(32f)
            leftMargin = SizeUtils.dp2px(32f)
        }
    }
    this.wrapper.requestLayout()
}

/***
 * Dialog Background properties For Alert Dialog
 * */
fun AlertDialog.background(
    dialogBackgroundColor: Int? = null
): AlertDialog {
    if (dialogBackgroundColor != null) {
        this.mainLayout.setBackgroundResource(dialogBackgroundColor)
    }
    return this
}

/***
 * Positions of Alert Dialog
 * */
fun AlertDialog.position(
    position: PositionDialogAlter.POSITIONS = PositionDialogAlter.POSITIONS.BOTTOM
): AlertDialog {
    val layoutParams = mainLayout.layoutParams as RelativeLayout.LayoutParams
    if (position == PositionDialogAlter.POSITIONS.CENTER) {
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
    } else if (position == PositionDialogAlter.POSITIONS.BOTTOM) {
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
    }
    mainLayout?.layoutParams = layoutParams
    return this
}

/***
 * Sub Title or Body of Alert Dialog
 * */
fun AlertDialog.body(
    body: CharSequence,
    fontStyle: Typeface? = null,
    color: Int = 0
): AlertDialog {
    this.subHeading.text = body.trim()
    this.subHeading.show()
    if (fontStyle != null) {
        this.subHeading.typeface = fontStyle
    }
    if (color != 0) {
        this.subHeading.setTextColor(color)
    }
    return this
}

/***
 * Icon of  Alert Dialog
 * */
fun AlertDialog.icon(
    icon: Int,
    animateIcon: Boolean = false
): AlertDialog {
    this.image.setImageResource(icon)
    this.image.show()
    // Pulse Animation for Icon
    if (animateIcon) {
        val pulseAnimation = AnimationUtils.loadAnimation(context, R.anim.pulse)
        image.startAnimation(pulseAnimation)
    }
    return this
}

/***
 * onPositive Button Properties For Alert Dialog
 *
 * No Need to call dismiss(). It is calling already
 * */
fun AlertDialog.onPositive(
    text: String,
    buttonBackgroundColor: Int? = null,
    textColor: Int? = null,
    action: (() -> Unit)? = null
): AlertDialog {
    this.yesButton.show()
    if (buttonBackgroundColor != null) {
        this.yesButton.backgroundTintList = ContextCompat.getColorStateList(context,buttonBackgroundColor ?: 0)
    }
    if (textColor != null) {
        this.yesButton.setTextColor(textColor)
    }
    this.yesButton.text = text.trim()
    this.yesButton.setOnClickListener {
        action?.invoke()
        dismiss()
    }
    return this
}

/***
 * onNegative Button Properties For Alert Dialog
 *
 * No Need to call dismiss(). It is calling already
 * */
fun AlertDialog.onNegative(
    text: String ,
    @ColorRes buttonBackgroundColor: Int? = null,
    textColor: Int? = null,
    action: (() -> Unit)? = null
): AlertDialog {
    this.noButton.show()
    this.noButton.text = text.trim()
    if (textColor != null) {
        this.noButton.setTextColor(textColor)
    }
    if (buttonBackgroundColor != null) {
        this.noButton.backgroundTintList = ContextCompat.getColorStateList(context,buttonBackgroundColor ?: 0)
        //this.noButton.setBackgroundResource(buttonBackgroundColor)
    }
    this.noButton.setOnClickListener {
        action?.invoke()
        dismiss()
    }
    return this
}

fun AlertDialog.isDismissTouchOutSide(`is`: Boolean): AlertDialog {
    setCancelable(`is`)
    setCanceledOnTouchOutside(`is`)
    setOnCancelListener {
        "cancel".log()
    }
    return this
}

private fun View.show() {
    this.visibility = View.VISIBLE
}