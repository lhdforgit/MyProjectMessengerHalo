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
import com.halo.common.utils.ktx.setLightStatusBar
import com.halo.widget.R
import com.halo.widget.bottomdrawer.utils.BottomDialog
import com.halo.widget.bottomdrawer.utils.BottomDrawerDelegateV3

class BottomDrawerDialogV3(context: Context?, @StyleRes theme: Int = R.style.BottomDialogThemeV3) :
    AppCompatDialog(context, theme), BottomDialog {

    val bottomDrawerDelegate = BottomDrawerDelegateV3(this.context, this)

    val drawer: BottomDrawerV3?
        get() = bottomDrawerDelegate.drawer

    val behavior: BottomSheetBehavior<BottomDrawerV3>?
        get() = bottomDrawerDelegate.behavior

    init {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.let {
            setLightStatusBar(it)
            it.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                it.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                it.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                it.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                it.statusBarColor = Color.TRANSPARENT
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

    override fun setContentView(@LayoutRes layoutResId: Int) {
        super.setContentView(bottomDrawerDelegate.wrapInBottomSheet(layoutResId, null, null))
    }

    override fun setContentView(view: View) {
        super.setContentView(bottomDrawerDelegate.wrapInBottomSheet(0, view, null))
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams?) {
        super.setContentView(bottomDrawerDelegate.wrapInBottomSheet(0, view, params))
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

    fun draggable(`is`: Boolean) {
        behavior?.isDraggable = `is`
    }

    companion object {
        inline fun build(
            context: Context?,
            block: Builder.() -> Unit
        ): BottomDrawerDialogV3 {
            return Builder(context!!)
                .apply(block)
                .build()
        }
    }

    class Builder(val context: Context) {
        var theme: Int = R.style.BottomDialogThemeV3
        var handleView: View? = null
        var isCancelableOnTouchOutside: Boolean? = null
        var ratioExpandState: Float? = null
        var isSkipCollapsed: Boolean = false
        var isWrapContentHeight: Boolean = false
        var marginTopDrawer: Int? = null
        fun build() = BottomDrawerDialogV3(context, theme).apply {

            whenNotNull(this@Builder.isCancelableOnTouchOutside) {
                bottomDrawerDelegate.isCancelableOnTouchOutside = it
            }

            bottomDrawerDelegate.isSkipCollapsed = isSkipCollapsed

            bottomDrawerDelegate.isWrapContentHeight = isWrapContentHeight

            whenNotNull(ratioExpandState) {
                bottomDrawerDelegate.ratioExpandedState = it
            }

            whenNotNull(marginTopDrawer) {
                bottomDrawerDelegate.marginTopDrawer = it
            }

            whenNotNull(this@Builder.handleView) {
                bottomDrawerDelegate.handleView = it
            }

        }

        private inline fun <T : Any, R> whenNotNull(input: T?, callback: (T) -> R): R? {
            return input?.let(callback)
        }
    }
}