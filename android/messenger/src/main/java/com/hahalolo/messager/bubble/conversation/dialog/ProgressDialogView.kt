package com.hahalolo.messager.bubble.conversation.dialog

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.hahalolo.messenger.databinding.ProgressDialogViewBinding

class ProgressDialogView : FrameLayout {

    private lateinit var binding: ProgressDialogViewBinding

    constructor(context: Context) : super(context){
        initView()
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        initView()
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        initView()
    }

    private fun initView() {
        val inflater = LayoutInflater.from(context)
        binding = ProgressDialogViewBinding.inflate(inflater, null)
        binding.root.setOnClickListener {  }
        addView(binding.root)
    }

    fun show() {
        kotlin.runCatching {
            visibility = View.VISIBLE
            binding.animationView.apply {
                this.playAnimation()
            }
        }
    }

    fun dismiss() {
        kotlin.runCatching {
            visibility = View.GONE
            binding.animationView.apply {
                this.cancelAnimation()
            }
        }
    }

}