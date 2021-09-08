package com.hahalolo.messager.presentation.message.adapter.holder.message

import android.view.View
import androidx.databinding.ViewDataBinding
import com.hahalolo.messager.chatkit.adapter.ChatListener
import com.hahalolo.messager.presentation.message.adapter.ChatMessageGroupType
import com.hahalolo.messager.presentation.message.adapter.ChatMessageModel
import com.hahalolo.messager.presentation.message.adapter.holder.ChatMessageViewHolder
import com.hahalolo.messenger.databinding.ChatTextIncomingItemBinding
import com.hahalolo.messenger.databinding.ChatTextOutcomingItemBinding
import com.halo.data.entities.message.Message
import com.halo.data.room.entity.MessageEntity

abstract class ChatMessageLinkViewHolder(
    binding: ViewDataBinding,
      chatListener: ChatListener
) : ChatMessageItemViewHolder(binding, chatListener) {

    override fun onBind(message: MessageEntity) {

    }

    class InComing(binding: ChatTextIncomingItemBinding, chatListener: ChatListener)
        : ChatMessageLinkViewHolder(binding, chatListener)

    class OutComing(binding: ChatTextOutcomingItemBinding, chatListener: ChatListener)
        : ChatMessageLinkViewHolder(binding, chatListener)
}