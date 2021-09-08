package com.hahalolo.messager.presentation.message.adapter.holder.message

import android.widget.TextView
import androidx.databinding.ViewDataBinding
import com.hahalolo.messager.chatkit.adapter.ChatListener
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatTypingIncomingItemBinding
import com.hahalolo.messenger.databinding.ChatTypingOutcomingItemBinding
import com.halo.data.room.entity.MessageEntity

abstract class ChatMessageTypingViewHolder(
    binding: ViewDataBinding,
    chatListener: ChatListener
) : ChatMessageItemViewHolder(binding, chatListener) {

    private val messageText: TextView? = itemView.findViewById(R.id.messageText) as? TextView

    override fun onBind(message: MessageEntity) {
        messageText?.text = "Typing..."

    }

    class InComing(binding: ChatTypingIncomingItemBinding, chatListener: ChatListener) :
        ChatMessageTypingViewHolder(binding, chatListener)

    class OutComing(binding: ChatTypingOutcomingItemBinding, chatListener: ChatListener) :
        ChatMessageTypingViewHolder(binding, chatListener)
}