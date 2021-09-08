/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.holder.text

import android.view.LayoutInflater
import android.view.ViewGroup
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
import com.hahalolo.messenger.databinding.ChatTextOutcomingItemBinding

class ChatTextOutComingItem(
    binding: ChatTextOutcomingItemBinding,
    chatListener: ChatListener,
    holderListener: HolderListener,
    imageLoader: ImageLoader,
    style: MessagesListStyle
) : ChatOutComingItem<ChatTextOutcomingItemBinding>(
    binding,
    chatListener,
    holderListener,
    imageLoader,
    style
) {


    companion object {
        fun create(
            parent: ViewGroup,
            chatListener: ChatListener,
            holderListener: HolderListener,
            imageLoader: ImageLoader, style: MessagesListStyle
        ): ChatTextOutComingItem {
            val binding = DataBindingUtil.inflate<ChatTextOutcomingItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.chat_text_outcoming_item,
                parent,
                false
            )
            return ChatTextOutComingItem(binding, chatListener, holderListener, imageLoader, style)
        }
    }

    override fun initView(binding: ChatTextOutcomingItemBinding) {
        super.initView(binding)
    }

    override fun onBind(iMessage: MessageEntity) {
        super.onBind(iMessage)
        setBubbleSize()
        bindText(binding.messageText, false, iMessage)
        bindActionReactions(binding.messageText, iMessage)
        bindDropLikeAndShowTime(iMessage, binding.messageText)
        bindDropLikeAndShowTime(iMessage, binding.messageBubble)
        bindSwipe(iMessage)
    }

    override fun onMessageGroupType(messageGroupType: MessageGroupType) {
        super.onMessageGroupType(messageGroupType)
        binding.messageBubble.apply {
            ViewCompat.setBackground(this, style.getOutcomingBubbleDrawable(messageGroupType))
        }
    }

    override fun applyStyle(style: MessagesListStyle) {
        super.applyStyle(style)
        binding.messageBubble.apply {
            this.setPadding(
                style.incomingDefaultBubblePaddingLeft,
                style.incomingDefaultBubblePaddingTop,
                style.incomingDefaultBubblePaddingRight,
                style.incomingDefaultBubblePaddingBottom
            )
            ViewCompat.setBackground(this, style.getOutcomingBubbleDrawable())
        }
    }

    override fun invalidateLayout(requestManager: RequestManager?) {
        super.invalidateLayout(requestManager)
        binding.messageText.invalidateLayout()
    }
}