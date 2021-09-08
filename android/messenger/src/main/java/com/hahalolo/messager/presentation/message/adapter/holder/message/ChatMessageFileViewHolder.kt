package com.hahalolo.messager.presentation.message.adapter.holder.message

import androidx.databinding.ViewDataBinding
import com.hahalolo.messager.chatkit.adapter.ChatListener
import com.hahalolo.messenger.databinding.ChatTextIncomingItemBinding
import com.hahalolo.messenger.databinding.ChatTextOutcomingItemBinding
import com.halo.data.room.entity.MessageEntity

abstract class ChatMessageFileViewHolder(
    binding: ViewDataBinding,
      chatListener: ChatListener
) : ChatMessageItemViewHolder(binding, chatListener) {

    override fun onBind(message: MessageEntity) {

    }

    class InComing(binding: ChatTextIncomingItemBinding, chatListener: ChatListener)
        : ChatMessageFileViewHolder(binding, chatListener)

    class OutComing(binding: ChatTextOutcomingItemBinding, chatListener: ChatListener)
        : ChatMessageFileViewHolder(binding, chatListener)
}