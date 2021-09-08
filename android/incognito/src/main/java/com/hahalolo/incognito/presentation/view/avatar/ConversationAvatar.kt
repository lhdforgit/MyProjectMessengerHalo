package com.hahalolo.incognito.presentation.view.avatar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoConversationAvatarViewBinding

class ConversationAvatar : FrameLayout {

    constructor(context: Context) : this(context, null) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private lateinit var binding: IncognitoConversationAvatarViewBinding

    private fun initView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.incognito_conversation_avatar_view, this, false)
        addView(binding.root)
        initializeLayout()

    }
    private fun initializeLayout() {

    }
}