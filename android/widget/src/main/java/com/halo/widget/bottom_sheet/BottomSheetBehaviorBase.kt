package com.halo.widget.bottom_sheet

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.halo.common.utils.SizeUtils
import com.halo.common.utils.layoutHeight
import com.halo.widget.R


@Suppress("UNUSED_CHANGED_VALUE")
abstract class BottomSheetBehaviorBase : BottomSheetDialogFragment() {
    private var modalLayout: View? = null
    private var containerView: LinearLayout? = null
    private var headerView: LinearLayout? = null
    private var behavior: BottomSheetBehavior<FrameLayout>? = null
    private var dividerTopView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetBehavior)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        containerView = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            removeAllViews()
        }
        val viewHeader = View.inflate(context, R.layout.hearder_view, null)?.apply {
            this.visibility = View.GONE
        }
        dividerTopView = View(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                SizeUtils.dp2px(0.75f))
            background = ContextCompat.getDrawable(context, R.color.divider)
        }
        containerView?.addView(dividerTopView)
        containerView?.addView(viewHeader)
        containerView?.addView(initializeCustomView(inflater, container, savedInstanceState))
        headerView = containerView?.findViewById(R.id.header_view)
        return containerView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //"onViewCreated :..".Log()
        super.onViewCreated(view, savedInstanceState)
        view.viewTreeObserver
            .addOnGlobalLayoutListener {
                val dialogView = dialog as BottomSheetDialog
                val bottomSheet = dialogView.findViewById<FrameLayout>(R.id.design_bottom_sheet)
                bottomSheet?.background =
                    ContextCompat.getDrawable(requireContext(), R.color.transparent)
                bottomSheet?.let { _bottomSheet ->
                    if (behavior == null) {
                        behavior = BottomSheetBehavior.from(_bottomSheet)
                        behavior?.state = BottomSheetBehavior.STATE_COLLAPSED
                        behavior?.skipCollapsed = true
                        behavior?.isHideable = false
                        behavior?.peekHeight = modalLayout?.height ?: 0//binding.modalLayout.height

                        //"onViewCreated :..".Log()
                        behavior?.addBottomSheetCallback(object :
                            BottomSheetBehavior.BottomSheetCallback() {
                            override fun onStateChanged(bottomSheet: View, newState: Int) {

                            }

                            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                                //"slideOffset :...${slideOffset} ".Log()

                                var offset = slideOffset
                                offset++
                                fun getStatusBarHeight(): Int {
                                    var result = 0
                                    val resourceId =
                                        resources.getIdentifier("status_bar_height",
                                            "dimen",
                                            "android")
                                    if (resourceId > 0) {
                                        result = resources.getDimensionPixelSize(resourceId)
                                    }
                                    return result
                                }

                                val heightBar = getStatusBarHeight()
                                headerView?.layoutHeight =
                                    (heightBar * slideOffset).toInt()

                                if (slideOffset <= 0f) {
                                    dividerTopView?.visibility = View.VISIBLE
                                    headerView?.visibility = View.INVISIBLE
                                } else {
                                    dividerTopView?.visibility = View.INVISIBLE
                                    headerView?.visibility = View.VISIBLE
                                }

                                //Todo : radius...
                                val maxCurve = 12
                                val curve = maxCurve * slideOffset
                                val shape = GradientDrawable()
                                shape.shape = GradientDrawable.RECTANGLE
                                shape.setColor(Color.WHITE)
                                shape.setStroke(2,
                                    ContextCompat.getColor(requireContext(), R.color.divider))
                                shape.cornerRadii = floatArrayOf(
                                    SizeUtils.dp2px(curve).toFloat(),
                                    SizeUtils.dp2px(curve).toFloat(),
                                    SizeUtils.dp2px(curve).toFloat(),
                                    SizeUtils.dp2px(curve).toFloat(),
                                    0f,
                                    0f,
                                    0f,
                                    0f
                                )
                                headerView?.background = shape
                            }
                        })
                    }
                }
            }
    }

    fun setModalLayout(view: View) {
        modalLayout = view
    }

    abstract fun initializeCustomView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View?
}