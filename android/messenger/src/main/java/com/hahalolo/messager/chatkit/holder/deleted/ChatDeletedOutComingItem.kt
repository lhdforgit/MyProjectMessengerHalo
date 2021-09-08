/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.holder.deleted

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import com.hahalolo.messager.chatkit.adapter.ChatListener
import com.hahalolo.messager.chatkit.commons.ImageLoader
import com.hahalolo.messager.chatkit.holder.ChatOutComingItem
import com.hahalolo.messager.chatkit.holder.HolderListener
import com.hahalolo.messager.chatkit.holder.MessageGroupType
import com.hahalolo.messager.chatkit.messages.MessagesListStyle
import com.halo.data.room.entity.MessageEntity
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatDeleteOutcomingItemBinding

class ChatDeletedOutComingItem(binding: ChatDeleteOutcomingItemBinding,
                               chatListener: ChatListener,
                               holderListener: HolderListener,
                               imageLoader: ImageLoader,
                               style: MessagesListStyle
)
    : ChatOutComingItem<ChatDeleteOutcomingItemBinding>(binding,
    chatListener,
    holderListener,
    imageLoader,
    style) {

    companion object {
        fun create(parent: ViewGroup, chatListener: ChatListener, holderListener: HolderListener,
                   imageLoader: ImageLoader,
                   style: MessagesListStyle)
                : ChatDeletedOutComingItem {

            val binding = DataBindingUtil.inflate<ChatDeleteOutcomingItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.chat_delete_outcoming_item,
                parent,
                false
            )
            return ChatDeletedOutComingItem(
                binding,
                chatListener,
                holderListener,
                imageLoader,
                style
            )
        }
    }

    override fun onBind(iMessage: MessageEntity) {
        super.onBind(iMessage)
        setBubbleSize()
        if (iMessage.isDeleted()){
            binding.messageText.text = itemView.context.getString(R.string.chat_message_status_delete)
        }else{
            binding.messageText.text = itemView.context.getString(R.string.chat_message_status_revoke)
        }
        bindDropLikeAndShowTime(iMessage, binding.messageText)
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
            ViewCompat.setBackground(this, style.getIncomingBubbleDrawable())
        }
    }
}