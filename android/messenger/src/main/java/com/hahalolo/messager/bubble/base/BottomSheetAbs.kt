package com.hahalolo.messager.bubble.base

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.BottomSheetAbsViewBinding
import com.halo.widget.anim.ViewAnim

abstract class BottomSheetAbs : LinearLayout {

    private lateinit var binding: BottomSheetAbsViewBinding

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
    }

    private fun initView(context: Context) {
        val inflater = LayoutInflater.from(context)
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_abs_view, null, false)
        addView(
            binding.root,
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        binding.container.addView(onCreateView(inflater, this))
        bindAction()
        intLayout()
    }

    private fun bindAction() {
        binding.apply {
            root.setOnClickListener {  }
            root.setOnTouchListener(object : OnTouchListener {
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    if (event?.action == MotionEvent.ACTION_DOWN) {
                        hide()
                    }
                    return false
                }
            })
        }
    }

    fun show() {
        animView(true)
    }

    fun hide() {
        animView(false)
    }

    fun hideDialog(){
        visibility = View.GONE
    }

    private fun animView(isShow: Boolean) {
        ViewAnim.Builder.create(binding.bottomSheetGr)
            .duration(300)
            .showDown()
            .hideDown()
            .show(isShow)

        ViewAnim.Builder.create(this)
            .duration(200)
            .show(isShow)
    }

    abstract fun onCreateView(inflater: LayoutInflater, viewGroup: ViewGroup): View

    abstract fun intLayout()

}