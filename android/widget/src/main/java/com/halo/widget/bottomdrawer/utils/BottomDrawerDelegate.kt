/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.bottomdrawer.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.halo.widget.R
import com.halo.widget.bottomdrawer.BottomDrawer
import java.util.concurrent.CopyOnWriteArrayList

class BottomDrawerDelegate(
    private val context: Context,
    private val dialog: BottomDialog
) {

    internal var behavior: BottomSheetBehavior<BottomDrawer>? = null
    var drawer: BottomDrawer? = null
    private lateinit var coordinator: CoordinatorLayout
    private val callbacks: CopyOnWriteArrayList<BottomSheetBehavior.BottomSheetCallback> =
        CopyOnWriteArrayList()

    private var offset = 0f
    internal var isCancelableOnTouchOutside = true
    internal var handleView: View? = null

    @SuppressLint("ClickableViewAccessibility")
    fun wrapInBottomSheet(
        layoutResId: Int,
        view: View?,
        params: ViewGroup.LayoutParams?
    ): View {
        var wrappedView = view
        val container = View.inflate(context, R.layout.bottom_drawer_layout, null) as FrameLayout
        coordinator = container.findViewById(R.id.bottom_sheet_coordinator)
        if (layoutResId != 0 && wrappedView == null) {
            wrappedView = LayoutInflater.from(context).inflate(layoutResId, coordinator, false)
        }

        // open full screen

        drawer = coordinator.findViewById<View>(R.id.bottom_sheet_drawer) as BottomDrawer
        drawer?.apply {
            behavior = BottomSheetBehavior.from(this)
        }
        behavior?.state = BottomSheetBehavior.STATE_HIDDEN
        val metrics = context.resources.displayMetrics
        behavior?.peekHeight = metrics.heightPixels / 2
        behavior?.isHideable = true

        if (params == null) {
            drawer?.addView(wrappedView)
        } else {
            drawer?.addView(wrappedView, params)
        }
        drawer?.addHandleView(handleView)

        coordinator.background.alpha = offset.toInt()

        behavior?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(sheet: View, state: Int) {
                callbacks.forEach { callback ->
                    callback.onStateChanged(sheet, state)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                callbacks.forEach { callback ->
                    callback.onSlide(bottomSheet, slideOffset)
                }
            }
        })

        addBottomSheetCallback {
            onSlide { _: View, slideOffset: Float ->
                offset = if (slideOffset != slideOffset) {
                    0f
                } else {
                    slideOffset
                }

                offset++
                updateBackgroundOffset()
                drawer?.onSlide(offset / 2f)
            }

            onStateChanged { _: View, newState: Int ->
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> dialog.onDismiss()
                    else -> {

                    }
                }
            }
        }

        coordinator.findViewById<View>(R.id.touch_outside)
            .setOnClickListener {
                if (isCancelableOnTouchOutside) {
                    behavior?.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }

        // Handle accessibility events
        drawer?.apply {
            ViewCompat.setAccessibilityDelegate(
                this,
                object : AccessibilityDelegateCompat() {
                    override fun onInitializeAccessibilityNodeInfo(
                        host: View, info: AccessibilityNodeInfoCompat
                    ) {
                        super.onInitializeAccessibilityNodeInfo(host, info)
                        info.isDismissable = true
                    }

                    override fun performAccessibilityAction(
                        host: View,
                        action: Int,
                        args: Bundle
                    ): Boolean {
                        if (action == AccessibilityNodeInfoCompat.ACTION_DISMISS) {
                            dialog.onCancel()
                            return true
                        }
                        return super.performAccessibilityAction(host, action, args)
                    }
                })

            setOnTouchListener { view, event ->
                // Consume the event and prevent it from falling through
                true
            }
        }
        return container
    }

    fun addBottomSheetCallback(func: BottomSheetCallback.() -> Unit): BottomSheetBehavior.BottomSheetCallback {
        val listener = BottomSheetCallback()
        listener.func()
        callbacks.add(listener)
        return listener
    }

    fun removeBottomSheetCallback(callback: BottomSheetBehavior.BottomSheetCallback) {
        callbacks.remove(callback)
    }

    private fun updateBackgroundOffset() {
        if (offset <= 1) {
            coordinator.background?.alpha = (255 * offset).toInt()
        } else {
            coordinator.background?.alpha = 255
        }
    }

    fun open() {
        Handler(Looper.getMainLooper()).postDelayed({
            behavior?.let {
                if (it.state == BottomSheetBehavior.STATE_HIDDEN) {
                    it.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                }
            }
        }, 50)
    }

    fun openFullScreen() {
//        drawer?.layoutParams = CoordinatorLayout.LayoutParams(
//            CoordinatorLayout.LayoutParams.MATCH_PARENT,
//            CoordinatorLayout.LayoutParams.MATCH_PARENT
//        )
//        drawer?.requestLayout()
//        coordinator.requestLayout()
        Handler(Looper.getMainLooper()).postDelayed({
            behavior?.let {
                it.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }, 50)
    }


    fun openExpanded() {
        Handler(Looper.getMainLooper()).postDelayed({
            behavior?.let {
                if (it.state == BottomSheetBehavior.STATE_HIDDEN) {
                    it.state = BottomSheetBehavior.STATE_EXPANDED
                } else if (it.state == BottomSheetBehavior.STATE_EXPANDED) {
                    it.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
        }, 50)
    }

    fun onBackPressed() {
        behavior?.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun onSaveInstanceState(superState: Bundle) {
        superState.putFloat("offset", offset)
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle) {
        offset = savedInstanceState.getFloat("offset")
        updateBackgroundOffset()
    }

    class BottomSheetCallback : BottomSheetBehavior.BottomSheetCallback() {
        private var _onSlide: ((view: View, slideOffset: Float) -> Unit)? = null
        private var _onStateChanged: ((view: View, state: Int) -> Unit)? = null

        override fun onSlide(view: View, slideOffset: Float) {
            _onSlide?.invoke(view, slideOffset)
        }

        fun onSlide(func: (view: View, slideOffset: Float) -> Unit) {
            _onSlide = func
        }

        override fun onStateChanged(view: View, state: Int) {
            _onStateChanged?.invoke(view, state)
        }

        fun onStateChanged(func: (view: View, state: Int) -> Unit) {
            _onStateChanged = func
        }
    }
}

interface BottomDialog {
    fun onDismiss()
    fun onCancel()
}