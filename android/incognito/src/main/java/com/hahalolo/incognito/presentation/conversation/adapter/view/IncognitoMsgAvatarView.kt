package com.hahalolo.incognito.presentation.conversation.adapter.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoMsgAvatarViewBinding

class IncognitoMsgAvatarView : FrameLayout {

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private lateinit var binding: IncognitoMsgAvatarViewBinding
    private fun initView() {
        binding = DataBindingUtil.inflate<IncognitoMsgAvatarViewBinding>(
            LayoutInflater.from(context),
            R.layout.incognito_msg_avatar_view, this, false
        )
    }
}