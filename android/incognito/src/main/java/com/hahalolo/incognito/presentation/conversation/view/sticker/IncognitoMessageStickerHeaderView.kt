package com.hahalolo.incognito.presentation.conversation.view.sticker

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoMessageStickerHeaderViewBinding

class IncognitoMessageStickerHeaderView : FrameLayout {
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

    private lateinit var binding: IncognitoMessageStickerHeaderViewBinding

    private var listener: IncognitoMessageStickerListener? = null

    fun updateListener(listener: IncognitoMessageStickerListener) {
        this.listener = listener
        bindLayout()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.incognito_message_sticker_header_view, this, false
        )
        addView(binding.root)
        initActions()
    }

    private fun initActions() {
        binding.btnGif.setOnClickListener {
            listener?.onSelectType(IncognitoMessageStickerType.GIF)
        }
        binding.btnSticker.setOnClickListener {
            listener?.onSelectType(IncognitoMessageStickerType.STICKER)
        }
        binding.btnHeart.setOnClickListener {
            listener?.onSelectType(IncognitoMessageStickerType.FAVORITE)
        }
        binding.btnEmoji.setOnClickListener {
            listener?.onSelectType(IncognitoMessageStickerType.EMOJI)
        }
    }

    private fun bindLayout() {
        listener?.observerStickerType(Observer {
            /*ICON*/
            binding.btnHeart.setImageResource(
                if (it == IncognitoMessageStickerType.FAVORITE)
                    R.drawable.ic_incognito_sticker_heart_active
                else R.drawable.ic_incognito_sticker_heart
            )

            binding.btnEmoji.setImageResource(
                if (it == IncognitoMessageStickerType.EMOJI)
                    R.drawable.ic_incognito_sticker_emoji_active
                else R.drawable.ic_incognito_sticker_emoji
            )

            binding.btnSticker.setImageResource(
                if (it == IncognitoMessageStickerType.STICKER)
                    R.drawable.ic_incognito_sticker_sticker_active
                else R.drawable.ic_incognito_sticker_sticker
            )

            binding.btnGif.setImageResource(
                if (it == IncognitoMessageStickerType.GIF)
                    R.drawable.ic_incognito_sticker_gif_active
                else R.drawable.ic_incognito_sticker_gif
            )

            /*BACKGROUND*/
            binding.btnHeart.setBackgroundResource(
                if (it == IncognitoMessageStickerType.FAVORITE)
                    R.drawable.bg_incognito_sticker_header_item_active
                else R.drawable.bg_incognito_sticker_header_item
            )

            binding.btnEmoji.setBackgroundResource(
                if (it == IncognitoMessageStickerType.EMOJI)
                    R.drawable.bg_incognito_sticker_header_item_active
                else R.drawable.bg_incognito_sticker_header_item
            )

            binding.btnSticker.setBackgroundResource(
                if (it == IncognitoMessageStickerType.STICKER)
                    R.drawable.bg_incognito_sticker_header_item_active
                else R.drawable.bg_incognito_sticker_header_item
            )

            binding.btnGif.setBackgroundResource(
                if (it == IncognitoMessageStickerType.GIF)
                    R.drawable.bg_incognito_sticker_header_item_active
                else R.drawable.bg_incognito_sticker_header_item
            )
        })
    }
}