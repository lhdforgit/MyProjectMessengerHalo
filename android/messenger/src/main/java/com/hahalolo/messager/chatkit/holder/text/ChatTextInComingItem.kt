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
import com.hahalolo.messager.chatkit.holder.ChatInComingItem
import com.hahalolo.messager.chatkit.holder.HolderListener
import com.hahalolo.messager.chatkit.holder.MessageGroupType
import com.hahalolo.messager.chatkit.messages.MessagesListStyle
import com.halo.data.room.entity.MessageEntity
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatTextIncomingItemBinding

class ChatTextInComingItem(
    binding: ChatTextIncomingItemBinding,
    chatListener: ChatListener,
    holderListener: HolderListener,
    imageLoader: ImageLoader,
    style: MessagesListStyle
) : ChatInComingItem<ChatTextIncomingItemBinding>(
    binding,
    chatListener,
    holderListener,
    imageLoader,
    style
) {

    companion object {
        fun create(parent: ViewGroup, chatListener: ChatListener, holderListener: HolderListener, imageLoader: ImageLoader, style: MessagesListStyle): ChatTextInComingItem {
            val binding = DataBindingUtil.inflate<ChatTextIncomingItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.chat_text_incoming_item,
                parent,
                false)
            return ChatTextInComingItem(binding, chatListener, holderListener, imageLoader, style)
        }
    }

    override fun initView(binding: ChatTextIncomingItemBinding) {
        super.initView(binding)
    }

    override fun onBind(iMessage: MessageEntity) {
        super.onBind(iMessage)
        setBubbleSize()
        bindText(binding.messageText, true, iMessage)
        bindActionReactions(binding.messageText, iMessage)
        bindDropLikeAndShowTime(iMessage, binding.messageText)
        bindDropLikeAndShowTime(iMessage, binding.messageBubble)
        bindSwipe(iMessage)
    }

    override fun onMessageGroupType(messageGroupType: MessageGroupType) {
        super.onMessageGroupType(messageGroupType)
        binding.messageBubble.apply {
            ViewCompat.setBackground(this, style.getIncomingBubbleDrawable(messageGroupType))
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
            ViewCompat.setBackground(this, style.getIncomingBubbleDrawable())
        }
    }

    override fun invalidateLayout(requestManager: RequestManager?) {
        super.invalidateLayout(requestManager)
        binding.messageText.invalidateLayout()
    }
}