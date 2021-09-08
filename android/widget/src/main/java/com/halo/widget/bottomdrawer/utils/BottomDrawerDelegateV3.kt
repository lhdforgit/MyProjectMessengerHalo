package com.halo.widget.bottomdrawer.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.halo.common.utils.SizeUtils
import com.halo.widget.R
import com.halo.widget.bottomdrawer.BottomDrawerV3
import com.halo.widget.databinding.LayoutBottomDrawerV3Binding
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.roundToInt

class BottomDrawerDelegateV3(
        private val context: Context,
        private val dialog: BottomDialog
) {
    val TAG
        get() = this::class.java.simpleName

    internal var behavior: BottomSheetBehavior<BottomDrawerV3>? = null

    val drawer: BottomDrawerV3?
        get() = binding?.bottomSheetDrawer

    var isWrapContentHeight: Boolean = false

    private val coordinator: CoordinatorLayout?
        get() = binding?.bottomSheetCoordinator

    var ratioExpandedState: Float = 9f.div(10f)
        set(value) {
            if (value !in 0f..1f) throw Error("ratio must in 0 to 1")
            field = value
        }

    var marginTopDrawer: Int = 0

    var ratioPeckHeight: Float = 2f.div(3f)
        set(value) {
            if (value !in 0f..1f) throw Error("ratio must in 0 to 1")
            field = value
        }

    var isInInsetNavigationBar = true

    var isSkipCollapsed = false
        set(value) {
            field = value
            behavior?.skipCollapsed = value
        }

    private val callbacks: CopyOnWriteArrayList<BottomSheetBehavior.BottomSheetCallback> =
            CopyOnWriteArrayList()
    private var offset = 0f
    var isCancelableOnTouchOutside = true
    internal var handleView: View? = null

    var binding: LayoutBottomDrawerV3Binding? = LayoutBottomDrawerV3Binding.inflate(
            LayoutInflater.from(context),
            null, false
    )

    private val displayMetrics
        get() = context.resources.displayMetrics

    private fun getNavigationBarHeight(): Int {
        val resources: Resources = context.resources
        val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    }

    @SuppressLint("ClickableViewAccessibility")
    fun wrapInBottomSheet(
            layoutResId: Int,
            view: View?,
            params: ViewGroup.LayoutParams?
    ): View {
        var wrappedView = view
        if (layoutResId != 0 && wrappedView == null) {
            wrappedView = LayoutInflater.from(context).inflate(layoutResId, coordinator, false)
        }
        drawer?.apply {
            val heightRatio =
                    (displayMetrics.heightPixels * ratioExpandedState).roundToInt() - marginTopDrawer
            /**
             * Chế độ full màn hình hay không
             * Nếu isWrapContentHeight == true => state expand sẽ wrap content
             * isSkipCollapsed thì sẽ bỏ qua luôn haft expand
             * Nếu isWrapContentHeight == false => state expand sẽ hiển thị theo ratio @see ratioExpandedState
             * */
            if (isWrapContentHeight) {
                val height = view?.getMeasurments()?.second?.plus(SizeUtils.dp2px(40f))
                if (height ?: 0 >= displayMetrics.heightPixels) {
                    drawer?.layoutParams?.height = heightRatio
                } else {
                    drawer?.layoutParams?.height = height ?: heightRatio
                }
            } else {
                drawer?.layoutParams?.height = heightRatio
            }
            behavior = BottomSheetBehavior.from(this)
            drawer?.requestLayout()
        }
        behavior?.apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isSkipCollapsed = false
            displayMetrics
                    .apply {
                        val height = if (isInInsetNavigationBar) {
                            (this.heightPixels / 2).plus(
                                    getNavigationBarHeight()
                            )
                        } else {
                            (this.heightPixels / 2)
                        }
                        peekHeight = height
                        halfExpandedRatio =
                                height.toFloat().div(heightPixels)
                    }

            isHideable = true
        }

        drawer?.apply {
            if (params == null) {
                addView(wrappedView)
            } else {
                addView(wrappedView, params)
            }
            addHandleView(handleView)
        }

        binding?.bottomSheetCoordinator?.background?.alpha = offset.toInt()

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

        coordinator?.findViewById<View>(R.id.touch_outside)
                ?.setOnClickListener {
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
        return binding?.container ?: wrappedView!!
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

    private fun View.getMeasurments(): Pair<Int, Int> {
        measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val width = measuredWidth
        val height = measuredHeight
        return width to height
    }


    fun setExpandFullScreen() {
        ratioExpandedState = 1f
    }

    private fun updateBackgroundOffset() {
        if (offset <= 1) {
            coordinator?.background?.alpha = (255 * offset).toInt()
        } else {
            coordinator?.background?.alpha = 255
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