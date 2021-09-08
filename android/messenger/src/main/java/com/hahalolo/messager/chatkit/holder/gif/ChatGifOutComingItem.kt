/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.holder.gif

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.RequestManager
import com.hahalolo.messager.chatkit.adapter.ChatListener
import com.hahalolo.messager.chatkit.commons.ImageLoader
import com.hahalolo.messager.chatkit.holder.ChatOutComingItem
import com.hahalolo.messager.chatkit.holder.HolderListener
import com.hahalolo.messager.chatkit.holder.MessageGroupType
import com.hahalolo.messager.chatkit.messages.MessagesListStyle
import com.halo.data.room.entity.MessageEntity
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatGifOutcomingItemBinding

class ChatGifOutComingItem(binding: ChatGifOutcomingItemBinding,
                           chatListener: ChatListener,
                           holderListener: HolderListener,
                           imageLoader: ImageLoader,
                           style: MessagesListStyle
)
    : ChatOutComingItem<ChatGifOutcomingItemBinding>(binding, chatListener, holderListener, imageLoader, style) {

    companion object {
        fun create(parent: ViewGroup,
                   chatListener: ChatListener,
                   holderListener: HolderListener,
                   imageLoader: ImageLoader,
                   style: MessagesListStyle)
                : ChatGifOutComingItem {

            val binding = DataBindingUtil.inflate<ChatGifOutcomingItemBinding>(LayoutInflater.from(parent.context),
                    R.layout.chat_gif_outcoming_item,
                    parent,
                    false)
            return ChatGifOutComingItem(binding!!, chatListener, holderListener, imageLoader, style)
        }
    }


    override fun onBind(iMessage: MessageEntity) {
        super.onBind(iMessage)
//
//        iMessage.attachmentTables?.firstOrNull()?.run {
//            setGiphySize(this.getStickerUrl(), binding.image, holderListener.getWIDTH_SCREEN())
//            bindStickerMedia(binding.image, this.getStickerUrl())
//        } ?: run {
//            binding.image.setImageResource(R.color.img_holder)
//        }
        bindActionReactions(binding.messageGif, iMessage)
        bindDropLikeAndShowTime(iMessage, binding.messageGif)
    }


    override fun onMessageGroupType(messageGroupType: MessageGroupType) {
        super.onMessageGroupType(messageGroupType)
        binding.messageOverlay.setBackgroundResource(
            style.getOutComingStickerOverlayResource(messageGroupType
            )
        )
    }

    override fun applyStyle(style: MessagesListStyle) {
        super.applyStyle(style)
        ViewCompat.setBackground(binding.messageOverlay, style.getOutcomingImageOverlayDrawable())
    }

    override fun invalidateLayout(requestManager: RequestManager?) {
        super.invalidateLayout(requestManager)
        binding.messageGif.run {
            requestManager?.clear(this)
            this.setImageDrawable(null)
        }
    }

    override fun getTargets(): MutableList<ImageView> {
        val result = super.getTargets()
        result.add(0, binding.messageGif)
        return result
    }
}