/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.holder.link

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import com.hahalolo.messager.chatkit.adapter.ChatListener
import com.hahalolo.messager.chatkit.commons.ImageLoader
import com.hahalolo.messager.chatkit.holder.ChatInComingItem
import com.hahalolo.messager.chatkit.holder.HolderListener
import com.hahalolo.messager.chatkit.holder.MessageGroupType
import com.hahalolo.messager.chatkit.messages.MessagesListStyle
import com.halo.data.room.entity.MessageEntity
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatLinkIncommingItemBinding

class ChatLinkInComingItem(binding: ChatLinkIncommingItemBinding,
                           chatListener: ChatListener,
                           holderListener: HolderListener,
                           imageLoader: ImageLoader,
                           style: MessagesListStyle
)
    : ChatInComingItem<ChatLinkIncommingItemBinding>(binding, chatListener, holderListener, imageLoader, style) {

    companion object {
        fun create(parent: ViewGroup,
                   chatListener: ChatListener,
                   holderListener: HolderListener,
                   imageLoader: ImageLoader,
                   style: MessagesListStyle): ChatLinkInComingItem {
            val binding = DataBindingUtil.inflate<ChatLinkIncommingItemBinding>(LayoutInflater.from(parent.context),
                    R.layout.chat_link_incomming_item,
                    parent,
                    false)
            return ChatLinkInComingItem(binding!!, chatListener, holderListener, imageLoader, style)
        }
    }

    override fun initView(binding: ChatLinkIncommingItemBinding) {
        super.initView(binding)
    }

    override fun onBind(iMessage: MessageEntity) {
        super.onBind(iMessage)
        //todo new update entity
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
            this.setPadding(style.incomingDefaultBubblePaddingLeft,
                    style.incomingDefaultBubblePaddingTop,
                    style.incomingDefaultBubblePaddingRight,
                    style.incomingDefaultBubblePaddingBottom)
            ViewCompat.setBackground(this, style.getIncomingBubbleDrawable())
        }
        binding.messageText.apply {
            super.configureLinksBehavior(this)
        }
    }
}