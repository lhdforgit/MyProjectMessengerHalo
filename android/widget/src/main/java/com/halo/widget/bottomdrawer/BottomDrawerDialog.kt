/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.bottomdrawer

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.halo.widget.R
import com.halo.widget.bottomdrawer.utils.BottomDialog
import com.halo.widget.bottomdrawer.utils.BottomDrawerDelegate
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class BottomDrawerDialog(context: Context?, @StyleRes theme: Int = R.style.BottomDialogTheme) :
    AppCompatDialog(context, theme), BottomDialog {

    val bottomDrawerDelegate = BottomDrawerDelegate(this.context, this)

    val drawer: BottomDrawer?
        get() = bottomDrawerDelegate.drawer

    val behavior: BottomSheetBehavior<BottomDrawer>?
        get() = bottomDrawerDelegate.behavior

    init {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.let {
            setLightStatusBar(it)
            setLightNavigationBar(it)
            it.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                it.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            }
            it.statusBarColor = Color.TRANSPARENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                it.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                it.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                var flags = it.decorView.systemUiVisibility
                flags =
                    flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    flags = flags xor View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                }
                it.decorView.systemUiVisibility = flags
            }
        }
    }

    private fun setLightStatusBar(window: Window) {
        if (Build.VERSION.SDK_INT < 23) return
        setSystemUiFlags(
            window,
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        )
    }

    private fun setSystemUiFlags(window: Window, flags: Int) {
        val view = window.decorView
        var uiFlags = view.systemUiVisibility
        uiFlags = uiFlags or flags
        view.systemUiVisibility = uiFlags
    }

    private fun setLightNavigationBar(window: Window) {
        if (Build.VERSION.SDK_INT < 27) return
        setSystemUiFlags(
            window,
            View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        )
    }

    override fun onStart() {
        super.onStart()
        open()
    }

    override fun setContentView(@LayoutRes layoutResId: Int) {
        super.setContentView(bottomDrawerDelegate.wrapInBottomSheet(layoutResId, null, null))
    }

    override fun setContentView(view: View) {
        super.setContentView(bottomDrawerDelegate.wrapInBottomSheet(0, view, null))
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams?) {
        super.setContentView(bottomDrawerDelegate.wrapInBottomSheet(0, view, params))
    }

    fun open() {
        bottomDrawerDelegate.open()
    }

    fun openFullScreen() {
        bottomDrawerDelegate.openFullScreen()
    }

    override fun onBackPressed() {
        bottomDrawerDelegate.onBackPressed()
    }

    override fun onSaveInstanceState(): Bundle {
        val superState = super.onSaveInstanceState()
        bottomDrawerDelegate.onSaveInstanceState(superState)
        return superState
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        bottomDrawerDelegate.onRestoreInstanceState(savedInstanceState)
    }

    override fun onDismiss() {
        dismiss()
    }

    override fun onCancel() {
        cancel()
    }

    companion object {
        inline fun build(
            context: Context?,
            block: Builder.() -> Unit
        ) = Builder(context!!)
            .apply(block)
            .build()
    }

    class Builder(val context: Context) {
        var theme: Int = R.style.BottomDialogTheme
        var handleView: View? = null
        var isCancelableOnTouchOutside: Boolean? = null
        var peekHeight: Int = 200

        private val dragAble = AtomicBoolean(true)

        private val stateExpand = AtomicInteger(BottomSheetBehavior.STATE_HALF_EXPANDED)

        fun stateExpand(state: Int) {
            stateExpand.set(state)
        }

        fun dragable(`is`: Boolean) {
            dragAble.set(`is`)
        }

        fun build() = BottomDrawerDialog(context, theme).apply {

            whenNotNull(this@Builder.isCancelableOnTouchOutside) {
                bottomDrawerDelegate.isCancelableOnTouchOutside = it
            }

            whenNotNull(this@Builder.handleView) {
                bottomDrawerDelegate.handleView = it
            }

            behavior?.apply {
                isDraggable = dragAble.compareAndSet(true, true)
                state = stateExpand.get()
                isFitToContents = true
                setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO, true)
            }

        }

        private inline fun <T : Any, R> whenNotNull(input: T?, callback: (T) -> R): R? {
            return input?.let(callback)
        }
    }
}