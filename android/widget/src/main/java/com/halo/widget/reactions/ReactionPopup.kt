/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.reactions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.core.view.GestureDetectorCompat
import com.halo.common.utils.UtilsVibrator


/**
 * @author ndn
 * Created by ndn
 * Created on 5/30/19.
 *
 * Entry point for reaction popup.
 */
class ReactionPopup @JvmOverloads constructor(
        var context: Context,
        var reactionsConfig: ReactionsConfig,
        var reactionSelectedListener: ReactionSelectedListener? = null,
        var reactionClickListener: ReactionClickListener? = null
) : PopupWindow(context), View.OnTouchListener, GestureDetector.OnGestureListener {

    private val rootView = FrameLayout(context).also {
        it.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
    }
    private val view: ReactionViewGroup by lazy(LazyThreadSafetyMode.NONE) {
        // Lazily inflate content during first display
        ReactionViewGroup(context, reactionsConfig).also {
            it.layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER)

            it.reactionSelectedListener = reactionSelectedListener

            rootView.addView(it)
        }.also { it.dismissListener = ::dismiss }
    }

    private val mDetector = GestureDetectorCompat(context, this)
    lateinit var v: View
    private val onSingleTapUpCallback: Boolean = reactionsConfig.onSingleTapUpCallback

    init {
        contentView = rootView
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.MATCH_PARENT
        isFocusable = true
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    /**
     * N???u ???????c set long click = false, khi ng?????i d??ng click v??o button
     * th?? hi???n th??? l??n Reaction View
     */
    override fun onDown(event: MotionEvent): Boolean {
        return false
    }

    override fun onFling(event1: MotionEvent, event2: MotionEvent,
                         velocityX: Float, velocityY: Float): Boolean {
        return false
    }

    /**
     * Khi ng?????i d??ng th???c hi???n h??nh ?????ng long click
     * Hi???n th??? l??n reaction
     */
    override fun onLongPress(event: MotionEvent) {
        if (!isShowing) {
            UtilsVibrator.startVibratorSmall(context)
            // Show fullscreen with button as context provider
            showAtLocation(v, Gravity.CENTER_HORIZONTAL, 0, 0)
            view.show(event, v)
        }
    }

    override fun onScroll(event1: MotionEvent, event2: MotionEvent, distanceX: Float,
                          distanceY: Float): Boolean {
        return false
    }

    override fun onShowPress(event: MotionEvent) {
    }

    /**
     * Th???c hi???n h??nh ?????ng khi ng?????i d??ng click v??o View.
     * Kh??ng hi???n th??? l??n Reaction m?? tr??? v??? s??? ki???n click
     */
    override fun onSingleTapUp(event: MotionEvent): Boolean {
        return if (onSingleTapUpCallback) {
            reactionClickListener?.invoke()
            true
        } else {
            if (!isShowing) {
                UtilsVibrator.startVibratorSmall(context)
                // Show fullscreen with button as context provider
                showAtLocation(v, Gravity.NO_GRAVITY, 0, 0)
                view.show(event, v)
            }
            false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        this.v = v
        if (this.mDetector.onTouchEvent(event)) {
            return true
        }
        return view.onTouchEvent(event)
    }

    override fun dismiss() {
        view.dismiss()
        super.dismiss()
    }
}
