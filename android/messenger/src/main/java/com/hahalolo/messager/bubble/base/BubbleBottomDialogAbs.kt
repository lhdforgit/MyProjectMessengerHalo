package com.hahalolo.messager.bubble.base

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import com.hahalolo.messager.bubble.getScreenSize
import com.halo.widget.anim.ViewAnim
import kotlin.math.roundToInt

abstract class BubbleBottomDialogAbs : AbsLifecycleView {

    abstract val listSize: Int?
    abstract val contentLayout: FrameLayout?

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun initializeLayout() {
        updateLayout()
        initAction()
    }

    override fun initializeViewModel() {

    }

    @SuppressLint("ClickableViewAccessibility")
    open fun initAction() {
        setOnTouchListener { v, event ->
            if (event?.action == MotionEvent.ACTION_DOWN) {
                hideDialog()
            }
            false
        }
        setOnClickListener {

        }
    }

    private fun initHeight() {
        var rato = 0.5
        listSize?.takeIf { it >= 5 }?.run {
            rato = 0.7
        }
        getScreenSize().run {
            heightPixels.let {
                contentLayout?.layoutParams?.apply {
                    height = (it * rato).roundToInt()
                }
            }
        }
    }

    private fun animView(isShow: Boolean) {
        ViewAnim.Builder.create(contentLayout)
            .duration(300)
            .showDown()
            .hideDown()
            .show(isShow)

        ViewAnim.Builder.create(this)
            .duration(200)
            .show(isShow)
    }

    fun showDialog() {
        animView(true)
        updateLayout()
    }

    fun hideDialog() {
        animView(false)
    }

    open fun updateLayout(){
        initHeight()
    }
}