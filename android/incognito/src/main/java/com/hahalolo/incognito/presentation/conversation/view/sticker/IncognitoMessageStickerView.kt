package com.hahalolo.incognito.presentation.conversation.view.sticker

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoMessageStickerViewBinding

class IncognitoMessageStickerView : FrameLayout {
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

    private lateinit var binding: IncognitoMessageStickerViewBinding

    private var listener: IncognitoMessageStickerListener? = null

    fun updateListener(listener: IncognitoMessageStickerListener) {
        this.listener = listener
        bindLayout()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.incognito_message_sticker_view,
            this, false
        )
        addView(binding.root)
    }

    private fun bindLayout() {
        listener?.run {
            binding.favorite.updateListener(this)
            binding.emoji.updateListener(this)
            binding.sticker.updateListener(this)
        }

        listener?.observerStickerType(Observer {

            if (it == IncognitoMessageStickerType.EMOJI){
                binding.emoji.resetEmojiPage()
            }

            binding.favorite.visibility =
                if (it == IncognitoMessageStickerType.FAVORITE) VISIBLE else GONE
            binding.emoji.visibility =
                if (it == IncognitoMessageStickerType.EMOJI) VISIBLE else GONE
            binding.sticker.visibility =
                if (it == IncognitoMessageStickerType.STICKER) VISIBLE else GONE
        })
    }
}