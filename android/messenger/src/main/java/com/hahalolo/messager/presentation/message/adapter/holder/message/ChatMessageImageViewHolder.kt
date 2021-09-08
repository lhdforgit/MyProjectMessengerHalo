package com.hahalolo.messager.presentation.message.adapter.holder.message

import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.hahalolo.messager.chatkit.adapter.ChatListener
import com.hahalolo.messager.chatkit.view.media.ChatMutilImageView
import com.hahalolo.messager.utils.Strings
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatImageIncomingItemBinding
import com.hahalolo.messenger.databinding.ChatImageOutcomingItemBinding
import com.halo.data.room.entity.MessageEntity

abstract class ChatMessageImageViewHolder(
    binding: ViewDataBinding,
      chatListener: ChatListener
) : ChatMessageItemViewHolder(binding,chatListener) {

    private var messageMedia: ChatMutilImageView? = itemView.findViewById(R.id.messageMedia)

    override fun onBind(message: MessageEntity) {
        messageMedia?.updateRequestManager(Glide.with(itemView.context))
        messageMedia?.updateListMedia(message.attachment())
        bindActionReactions(messageMedia, message)
    }

    class InComing(binding: ChatImageIncomingItemBinding, chatListener: ChatListener)
        : ChatMessageImageViewHolder(binding, chatListener)

    class OutComing(binding: ChatImageOutcomingItemBinding, chatListener: ChatListener)
        : ChatMessageImageViewHolder(binding, chatListener)
}