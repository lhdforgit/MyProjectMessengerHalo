package com.hahalolo.incognito.presentation.conversation.view.sticker.emoji

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.FrameLayout
import com.hahalolo.incognito.presentation.conversation.view.sticker.IncognitoMessageStickerListener
import com.halo.widget.emoji.EmojiImageView
import com.halo.widget.emoji.EmojiPagerView
import com.halo.widget.emoji.EmojiPopup
import com.halo.widget.emoji.EmojiView
import com.halo.widget.emoji.emoji.Emoji
import com.halo.widget.emoji.listeners.OnEmojiClickListener
import com.halo.widget.emoji.listeners.OnEmojiLongClickListener

class IncognitoMsgEmojiView : FrameLayout {

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

    private var listener: IncognitoMessageStickerListener? = null

    private var emojiView: EmojiPagerView?=null

    fun updateListener(listener: IncognitoMessageStickerListener) {
        this.listener = listener
        bindLayout()
    }

    private fun bindLayout() {
        removeAllViews()
        listener?.focusEditText()?.takeIf { emojiView==null }?.run {
             emojiView = EmojiPagerView(context, this)
            addView(
                emojiView,
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
    }

    fun resetEmojiPage(){
        emojiView?.setCurrentPage(1)
    }

    private fun initView() {

    }
}