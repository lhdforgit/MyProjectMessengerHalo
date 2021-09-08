package com.hahalolo.incognito.presentation.conversation.dialog.view.action

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoMsgActionViewBinding

class IncognitoMsgActionView : FrameLayout{

    constructor(context: Context) : super(context){initView()}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){initView()}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){initView()}

    private lateinit var binding: IncognitoMsgActionViewBinding
    private var listener: IncognitoMsgActionListener?=null

    fun updateListener(listener: IncognitoMsgActionListener){
        this.listener = listener
    }

    private fun initView(){
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),
        R.layout.incognito_msg_action_view, this, false )
        addView(binding.root)
    }
}