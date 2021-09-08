package com.hahalolo.incognito.presentation.conversation.dialog.view.message

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoMsgItemViewBinding

class IncognitoMsgItemView : FrameLayout {
    constructor(context: Context) : super(context) { initView() }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { initView() }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) { initView() }

    private lateinit var binding: IncognitoMsgItemViewBinding

    private var listener: IncognitoMsgItemListener?=null

    fun updateListener(listener: IncognitoMsgItemListener){
        this.listener = listener
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),
        R.layout.incognito_msg_item_view, this, false)
        addView(binding.root)
    }
}