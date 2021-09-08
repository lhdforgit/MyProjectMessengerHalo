/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.holder.replay

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
import com.hahalolo.messenger.databinding.ChatReplayIncomingItemBinding

class ChatReplayInComingItem(binding: ChatReplayIncomingItemBinding,
                             chatListener: ChatListener,
                             holderListener: HolderListener,
                             imageLoader: ImageLoader,
                             style: MessagesListStyle
)
    : ChatInComingItem<ChatReplayIncomingItemBinding>(binding, chatListener, holderListener, imageLoader, style) {

    companion object {
        fun create(parent: ViewGroup, chatListener: ChatListener,
                   holderListener: HolderListener,
                   imageLoader: ImageLoader,
                   style: MessagesListStyle): ChatReplayInComingItem {
            val binding = DataBindingUtil.inflate<ChatReplayIncomingItemBinding>(LayoutInflater.from(parent.context),
                    R.layout.chat_replay_incoming_item,
                    parent,
                    false)
            return ChatReplayInComingItem(binding, chatListener,holderListener, imageLoader, style)
        }
    }

    override fun initView(binding: ChatReplayIncomingItemBinding) {
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
//        binding.messageText.apply {
//            this.setTextColor(style.incomingTextColor)
//            this.setTextSize(TypedValue.COMPLEX_UNIT_PX, style.incomingTextSize.toFloat())
//            this.setTypeface(this.typeface, style.incomingTextStyle)
//
//            this.autoLinkMask = style.textAutoLinkMask
//            this.setLinkTextColor(style.incomingTextLinkColor)
//            super.configureLinksBehavior(this)
//        }
    }
}