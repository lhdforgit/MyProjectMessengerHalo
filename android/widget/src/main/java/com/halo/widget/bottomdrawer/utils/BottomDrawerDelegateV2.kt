package com.halo.widget.bottomdrawer.utils

//class BottomDrawerDelegateV2(
//    private val context: Context,
//    private val dialog: BottomDialog
//) {
//
//    internal var behavior: BottomSheetBehavior<BottomDrawerV2>? = null
//
//    val drawer: BottomDrawerV2
//        get() = binding.bottomSheetDrawer
//
//    private val coordinator
//        get() = binding.bottomSheetCoordinator
//
//    private val callbacks: CopyOnWriteArrayList<BottomSheetBehavior.BottomSheetCallback> =
//        CopyOnWriteArrayList()
//
//    private var offset = 0f
//    internal var isCancelableOnTouchOutside = true
//    internal var handleView: View? = null
//
//    private val binding: BottomDrawerV2Binding =
//        BottomDrawerV2Binding.inflate(LayoutInflater.from(context), null, false)
//
//    var heightDrawer: Int = 0
//
//    private val fullHeight = context.resources.displayMetrics.heightPixels
//
//    init {
//        binding.bottomSheetDrawer.container.viewTreeObserver.addOnGlobalLayoutListener {
//            resetHaftExpand()
//        }
//    }
//
//    private var isSet = false
//
//    private fun resetHaftExpand() {
//        heightDrawer = binding.bottomSheetDrawer.container.measuredHeight
//        val ratio = heightDrawer.toFloat().div(fullHeight.toFloat())
//        if (ratio in 0F..1F) {
//            if (!isSet) {
//                // open full screen
//                runCatching {
//                    behavior = BottomSheetBehavior.from(drawer).apply {
//                        isFitToContents = false
//                        halfExpandedRatio = ratio
//                        isHideable = true
//                    }
//                }
//            }
//            isSet = true
//        }
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    fun wrapInBottomSheet(
//        layoutResId: Int,
//        view: View?,
//        params: ViewGroup.LayoutParams?
//    ): View {
//        var wrappedView = view?.apply {
//            this.measuredHeight
//        }
//        if (layoutResId != 0 && wrappedView == null) {
//            wrappedView = LayoutInflater.from(context)
//                .inflate(layoutResId, coordinator, false)
//        }
//        if (params == null) {
//            drawer.addView(wrappedView)
//        } else {
//            drawer.addView(wrappedView, params)
//        }
//        drawer.addHandleView(handleView)
//        // open full screen
//        runCatching {
//            behavior = BottomSheetBehavior.from(drawer).apply {
//                state = BottomSheetBehavior.STATE_HIDDEN
//                val metrics = context.resources.displayMetrics
//                peekHeight = metrics.heightPixels / 2 * 3
//                isFitToContents = false
//                isHideable = true
//            }
//        }
//        coordinator.background.alpha = offset.toInt()
//
//        behavior?.addBottomSheetCallback(object :
//            BottomSheetBehavior.BottomSheetCallback() {
//            override fun onStateChanged(sheet: View, state: Int) {
//                callbacks.forEach { it.onStateChanged(sheet, state) }
//            }
//
//            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                callbacks.forEach { it.onSlide(bottomSheet, slideOffset) }
//            }
//        })
//
//        addBottomSheetCallback {
//            onSlide { _: View, slideOffset: Float ->
//                offset = if (slideOffset != slideOffset) {
//                    0f
//                } else {
//                    slideOffset
//                }
//                offset++
//                updateBackgroundOffset()
//                drawer.onSlide(offset * 3f / 2f)
//            }
//
//            onStateChanged { _: View, newState: Int ->
//                when (newState) {
//                    BottomSheetBehavior.STATE_HIDDEN -> dialog.onDismiss()
//                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
//
//                    }
//                    BottomSheetBehavior.STATE_EXPANDED -> {
//
//                    }
//                    BottomSheetBehavior.STATE_COLLAPSED -> {
//
//                    }
//                    else -> {
//
//                    }
//                }
//            }
//        }
//
//        binding.touchOutside.setOnClickListener {
//            if (isCancelableOnTouchOutside) {
//                behavior?.state = BottomSheetBehavior.STATE_HIDDEN
//            }
//        }
//
//        // Handle accessibility events
//        ViewCompat.setAccessibilityDelegate(
//            drawer,
//            object : AccessibilityDelegateCompat() {
//                override fun onInitializeAccessibilityNodeInfo(
//                    host: View,
//                    info: AccessibilityNodeInfoCompat
//                ) {
//                    super.onInitializeAccessibilityNodeInfo(host, info)
//                    info.isDismissable = true
//                }
//
//                override fun performAccessibilityAction(
//                    host: View,
//                    action: Int,
//                    args: Bundle
//                ): Boolean {
//                    if (action == AccessibilityNodeInfoCompat.ACTION_DISMISS) {
//                        dialog.onCancel()
//                        return true
//                    }
//                    return super.performAccessibilityAction(host, action, args)
//                }
//            })
//
//        drawer.setOnTouchListener { _, _ ->
//            true
//        }
//
//        return binding.root
//    }
//
//    fun addBottomSheetCallback(func: BottomSheetCallback.() -> Unit): BottomSheetBehavior.BottomSheetCallback {
//        val listener = BottomSheetCallback()
//        listener.func()
//        callbacks.add(listener)
//        return listener
//    }
//
//    fun removeBottomSheetCallback(callback: BottomSheetBehavior.BottomSheetCallback) {
//        callbacks.remove(callback)
//    }
//
//    private fun updateBackgroundOffset() {
//        if (offset <= 1) {
//            coordinator.background?.alpha = (255 * offset).toInt()
//        } else {
//            coordinator.background?.alpha = 255
//        }
//    }
//
//    fun open() {
//        Handler(Looper.getMainLooper()).postDelayed({
//            behavior?.let {
//                if (it.state == BottomSheetBehavior.STATE_HIDDEN) {
//                    it.state = BottomSheetBehavior.STATE_HALF_EXPANDED
//                }
//            }
//        }, 50)
//    }
//
//    fun openFullScreen() {
//        Handler(Looper.getMainLooper()).postDelayed({
//            behavior?.let {
//                it.state = BottomSheetBehavior.STATE_EXPANDED
//            }
//        }, 50)
//    }
//
//
//    fun openHaftExpand() {
//        Handler(Looper.getMainLooper()).postDelayed({
//            behavior?.let {
//                if (it.state == BottomSheetBehavior.STATE_HIDDEN) {
//                    it.state = BottomSheetBehavior.STATE_EXPANDED
//                } else if (it.state == BottomSheetBehavior.STATE_EXPANDED) {
//                    it.state = BottomSheetBehavior.STATE_HIDDEN
//                }
//            }
//        }, 50)
//    }
//
//    fun onBackPressed() {
//        behavior?.state = BottomSheetBehavior.STATE_HIDDEN
//    }
//
//    fun onSaveInstanceState(superState: Bundle) {
//        superState.putFloat("offset", offset)
//    }
//
//    fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        offset = savedInstanceState.getFloat("offset")
//        updateBackgroundOffset()
//    }
//
//    class BottomSheetCallback : BottomSheetBehavior.BottomSheetCallback() {
//        private var _onSlide: ((view: View, slideOffset: Float) -> Unit)? = null
//        private var _onStateChanged: ((view: View, state: Int) -> Unit)? = null
//
//        override fun onSlide(view: View, slideOffset: Float) {
//            _onSlide?.invoke(view, slideOffset)
//        }
//
//        fun onSlide(func: (view: View, slideOffset: Float) -> Unit) {
//            _onSlide = func
//        }
//
//        override fun onStateChanged(view: View, state: Int) {
//            _onStateChanged?.invoke(view, state)
//        }
//
//        fun onStateChanged(func: (view: View, state: Int) -> Unit) {
//            _onStateChanged = func
//        }
//    }
//}