/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.dialog

import android.content.Context
import android.graphics.Outline
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.TypedValue
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.halo.common.utils.SizeUtils
import com.halo.widget.R
import kotlinx.android.synthetic.main.bottom_sheet_dialog_base.view.*


/**
 *
 *
Ex:
class TestBottomSheet : BottomSheetDialogBase() {
private var onClick: () -> Unit = {}
override fun setUpDialog(dialog: CustomDialog) {
dialog.setTitleDialog("setTitleDialog") //  Khi TitleDialog = null or empty thì ẩn tv
//dialog.setContentBtn("setContentBtn") //  Khi contentbtn = null or empty thì ẩn btn
dialog.setOnClickPositive(onClick)
}
override fun initializeCustomView(
inflater: LayoutInflater,
container: ViewGroup?,
savedInstanceState: Bundle?
): View? {
val customView = View.inflate(context, R.layout.test_dialog, null)
initAction()
return customView
}
fun initAction() {
onClick = {
// detect onclick positive ...
"onClick ... ".Log()
}
}
}
 *
 *
 * */

abstract class BottomSheetDialogBase(private var contextView: Context? = null) :
    BottomSheetDialogFragment() {
    /**
     *
    Bottom sheets have 5 states:
    STATE_COLLAPSED: The bottom sheet is visible but only showing its peek height. This state is usually the 'resting position' of a Bottom Sheet. The peek height is chosen by the developer and should be enough to indicate there is extra content, allow the user to trigger an action or expand the bottom sheet.
    STATE_EXPANDED: The bottom sheet is visible and its maximum height and it is neither dragging or settling (see below).
    STATE_DRAGGING: The user is actively dragging the bottom sheet up or down.
    STATE_SETTLING: The bottom sheet is settling to specific height after a drag/swipe gesture. This will be the peek height, expanded height, or 0, in case the user action caused the bottom sheet to hide.
    STATE_HIDDEN: The bottom sheet is no longer visible.
     * */
    
    private val bottomSheetView: View by lazy {
        View.inflate(
            context,
            R.layout.bottom_sheet_dialog_base,
            null
        )
    }
    private val bottomSheetViewSmall: View by lazy {
        View.inflate(
            context,
            R.layout.bottom_sheet_dialog_base_small,
            null
        )
    }

    private var state: Int = BottomSheetBehavior.STATE_EXPANDED
    private var newStatic: Int? = null
    private var isCheckFullScreen: Boolean? = null
    private var onSlide: (bottomSheet: View, slideOffset: Float) -> Unit = { _, _ -> }
    private var mode: Mode? = Mode.NORMAL
    private var isDraggable: Boolean? = false
    private var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>? = null
    private var slideOffset: Float? = null
    private var isScrollHorizontal: Boolean? = false

    enum class Mode {
        SMALL, NORMAL
    }

    interface CustomDialog {
        fun setTitleDialog(title: String)
        fun setContentBtn(content: String)
        fun setOnClickPositive(onClick: () -> Unit)
        fun setState(state: Int = BottomSheetBehavior.STATE_COLLAPSED)
        fun setEnablePositive(isEnable: () -> Boolean)
        fun setIsScrollHorizontal(isScrollHorizontal: Boolean? = false)
        fun setMode(mode: Mode)
    }

    fun setIsDraggable(isDraggable: Boolean) {
        this.isDraggable = isDraggable
        bottomSheetBehavior?.isDraggable = isDraggable
        Handler().post {
            (dialog as? BottomSheetDialog)?.apply {
                val bottomSheetDialog =
                    this.findViewById<View>(R.id.design_bottom_sheet) as? FrameLayout
                bottomSheetDialog?.apply {
                    BottomSheetBehavior.from(this).isDraggable = isDraggable
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, com.halo.themes.R.style.BottomSheetDialogThemeBase)
    }

    fun setOnSlide(onSlide: (bottomSheet: View, slideOffset: Float) -> Unit) {
        this.onSlide = onSlide
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bottomSheetView.divider_img?.alpha = 1f
        bottomSheetView.close_btn?.alpha = 0f
        bottomSheetView.divider_bottom_view?.alpha = 0f

        //Todo : handle toolbar bottom sheet ...
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog?.setOnShowListener {
            Handler().post {
                (dialog as? BottomSheetDialog)?.apply {
                    val bottomSheetDialog =
                        this.findViewById<View>(R.id.design_bottom_sheet) as? FrameLayout
                    bottomSheetDialog?.apply {
                        bottomSheetBehavior = BottomSheetBehavior.from(this).apply {
                            // define state for bottom sheet ...
                            //"this@BottomSheetDialogBase.state:... ${this@BottomSheetDialogBase.state}".Log()

                            this.state = this@BottomSheetDialogBase.state
                            addBottomSheetCallback(object :
                                BottomSheetBehavior.BottomSheetCallback() {
                                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                                    //slideOffset.Log("slideOffset : ... ")
                                    this@BottomSheetDialogBase.slideOffset = slideOffset

                                    if (checkBottomSheetFullScreen(bottomSheet) == true) {
                                        this@BottomSheetDialogBase.onSlide(
                                            bottomSheet,
                                            slideOffset
                                        )
                                        val widthMax = SizeUtils.px2dp(250f)
                                        val widthDefault = SizeUtils.px2dp(20f)
                                        val duration = widthMax - widthDefault
                                        val offset = (widthMax - duration * slideOffset).toInt()
                                        val offsetCurrent =
                                            if (offset <= SizeUtils.px2dp(100f)) SizeUtils.px2dp(
                                                100f
                                            ) else offset
                                        bottomSheetView.divider_img?.layoutParams =
                                            LinearLayout.LayoutParams(
                                                offsetCurrent,
                                                bottomSheetView.divider_img?.height ?: 0
                                            ).apply {
                                                if (slideOffset >= 0.7) {
                                                    val currentMargin =
                                                        20 - (0.4 - (1 - slideOffset)) * 50
                                                    if (currentMargin >= 0.0) {
                                                        val margin =
                                                            SizeUtils.dp2px(currentMargin.toFloat())
                                                        setMargins(0, 0, 0, margin)
                                                    }
                                                }
                                            }
                                        when {
                                            slideOffset == 1f -> {
                                                bottomSheetView.divider_img?.visibility =
                                                    View.INVISIBLE
                                            }
                                            slideOffset >= 0.9 -> {
                                                bottomSheetView.divider_img?.alpha =
                                                    1 - slideOffset
                                                bottomSheetView.close_btn?.alpha = slideOffset
                                                bottomSheetView.divider_img?.visibility =
                                                    View.VISIBLE
                                            }
                                            (slideOffset >= 0.7) -> {
                                                val textSizeTitleMax = 23f
                                                val textSizeTitleExpanded = 17f
                                                val durationSize =
                                                    textSizeTitleMax - textSizeTitleExpanded
                                                val textSizeCurrent =
                                                    textSizeTitleMax - (durationSize * slideOffset)
                                                if (textSizeCurrent >= 17f) {
                                                    bottomSheetView.title_tv?.setTextSize(
                                                        TypedValue.COMPLEX_UNIT_SP,
                                                        textSizeCurrent
                                                    )
                                                }
                                                bottomSheetView.divider_bottom_view?.alpha =
                                                    slideOffset
                                                bottomSheetView.divider_img?.visibility =
                                                    View.VISIBLE
                                            }
                                            (slideOffset <= 0.02f) -> {
                                                if (this@BottomSheetDialogBase.newStatic == BottomSheetBehavior.STATE_SETTLING) {
                                                    dismiss()
                                                }
                                            }
                                            else -> {
                                                bottomSheetView.divider_img?.alpha = 1f
                                                bottomSheetView.close_btn?.alpha = 0f
                                                bottomSheetView.divider_img?.visibility =
                                                    View.VISIBLE
                                                bottomSheetView.divider_bottom_view?.alpha = 0f
                                            }
                                        }

                                        val maxCurve = 12
                                        val slideCurve =
                                            maxCurve - (maxCurve * slideOffset)
                                        val curveRadius = SizeUtils.dp2px(slideCurve).toFloat()
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            bottomSheetView.apply {
                                                outlineProvider =
                                                    object : ViewOutlineProvider() {
                                                        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                                                        override fun getOutline(
                                                            view: View?,
                                                            outline: Outline?
                                                        ) {
                                                            outline?.setRoundRect(
                                                                0, 0,
                                                                width,
                                                                (height + curveRadius).toInt(),
                                                                curveRadius
                                                            )
                                                        }
                                                    }
                                                clipToOutline = true
                                            }
                                        }
                                    } else {
                                        if (this@BottomSheetDialogBase.newStatic == BottomSheetBehavior.STATE_SETTLING) {
                                            //Todo :thanh work later...
//                                            if (slideOffset <= 0.02f) {
//                                                dismiss()
//                                            }
                                        }
                                    }
                                }

                                override fun onStateChanged(bottomSheet: View, newState: Int) {
                                    this@BottomSheetDialogBase.newStatic = newState
                                    var slideOffset =
                                        this@BottomSheetDialogBase.slideOffset ?: 0F
                                    if (slideOffset < 0) {
                                        slideOffset *= -1F
                                    }
                                    //"isScrollHorizontal :... $isScrollHorizontal ".Log()
                                    if (newState == BottomSheetBehavior.STATE_SETTLING) {
                                        if (isScrollHorizontal == true) {
                                            if (slideOffset >= 0.5f) {
                                                dismiss()
                                            }
                                        } else {
                                            dismiss()
                                        }
                                    }
                                }
                            })
                        }
                        //bottomSheetBehavior?.isDraggable = false
                    }
                }
            }
        }
        val curveRadius = SizeUtils.dp2px(12f).toFloat()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bottomSheetView.apply {
                outlineProvider = object : ViewOutlineProvider() {
                    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                    override fun getOutline(view: View?, outline: Outline?) {
                        outline?.setRoundRect(
                            0,
                            0,
                            width,
                            (height + curveRadius).toInt(),
                            curveRadius
                        )
                    }
                }
                clipToOutline = true
            }
        }
        val contentView = initializeCustomView(inflater, container, savedInstanceState)
        bottomSheetView.content_layout?.removeAllViews()
        bottomSheetView.content_layout?.addView(contentView)
        bottomSheetView.close_btn?.setOnClickListener {
            dismiss()
        }

        val customDialog = object : CustomDialog {
            override fun setTitleDialog(title: String) {
                if (!TextUtils.isEmpty(title)) {
                    bottomSheetView.title_tv?.visibility = View.VISIBLE
                    bottomSheetView.close_btn?.visibility = View.VISIBLE
                    bottomSheetView.divider_bottom_view?.visibility = View.VISIBLE
                    bottomSheetView.title_tv?.text = title
                }
            }

            override fun setContentBtn(content: String) {
                if (!TextUtils.isEmpty(content)) {
                    bottomSheetView.positive_btn?.visibility = View.VISIBLE
                    bottomSheetView.positive_btn?.text = content
                }
            }

            override fun setOnClickPositive(onClick: () -> Unit) {
                bottomSheetView.positive_btn?.setOnClickListener {
                    //"setOnClickPositive :...".Log()
                    onClick()
                }
            }

            override fun setState(state: Int) {
                this@BottomSheetDialogBase.state = state
            }

            override fun setEnablePositive(isEnable: () -> Boolean) {
                //" isEnable :...  ${isEnable()} ".Log()
                bottomSheetView.positive_btn?.isEnabled = isEnable()
            }

            override fun setIsScrollHorizontal(isScrollHorizontal: Boolean?) {
                this@BottomSheetDialogBase.isScrollHorizontal = isScrollHorizontal
            }

            override fun setMode(mode: Mode) {
                this@BottomSheetDialogBase.mode = mode
            }
        }
        setUpDialog(customDialog)

        return bottomSheetView /*when (this@BottomSheetDialogBase.mode) {
            Mode.SMALL -> {
                bottomSheetViewSmall
            }
            Mode.NORMAL -> {
                bottomSheetView
            }
            else -> {
                bottomSheetView
            }
        }*/
    }

    fun checkBottomSheetFullScreen(view: View): Boolean? {
        if (isCheckFullScreen == null) {
            val heightScreen = SizeUtils.dp2px(resources.configuration.screenHeightDp.toFloat())
            isCheckFullScreen = (heightScreen - view.height) <= 10
        }
        return isCheckFullScreen
    }

    abstract fun initializeCustomView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?

    abstract fun setUpDialog(dialog: CustomDialog)

    //Show toast above content view ...
    fun showToast(content: String) {
        val toast = Toast.makeText(
            context,
            content,
            Toast.LENGTH_SHORT
        )
        toast.view?.apply {
            background = context?.let {
                ContextCompat.getDrawable(it, R.drawable.bg_toast_filter)
            }
            findViewById<TextView>(android.R.id.message)?.let { _textView ->
                _textView.setTextColor(ContextCompat.getColor(context, R.color.app_background))
                _textView.textSize = 15f
                _textView.setLineSpacing(
                    1f,
                    1f
                )
            }
        }
        toast.setGravity(
            Gravity.BOTTOM or Gravity.CENTER,
            0,
            ((view?.height ?: 0) + SizeUtils.dp2px(15f))
        )
        toast.view
        toast.show()
    }


    fun show(): BottomSheetDialogFragment {
        val dialog = this@BottomSheetDialogBase
        (contextView as? FragmentActivity)?.apply {
            dialog.show(supportFragmentManager, "show_bottom_sheet")
        }
        return dialog
    }

    fun setEnablePositive(isEnable: Boolean) {
        bottomSheetView.positive_btn?.isEnabled = isEnable
    }

    fun setEnableDismiss(isEnable: Boolean){
        dialog?.setCancelable(isEnable)
    }

    fun setOnDismissListener(onClick: () -> Unit) {
        this.dialog?.setOnDismissListener {
            onClick()
        }
    }
}
