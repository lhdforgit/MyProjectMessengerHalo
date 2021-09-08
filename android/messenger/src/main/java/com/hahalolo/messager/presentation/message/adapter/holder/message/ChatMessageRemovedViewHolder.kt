package com.hahalolo.messager.presentation.message.adapter.holder.message

import android.view.View
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import com.hahalolo.messager.chatkit.adapter.ChatListener
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatDeleteIncomingItemBinding
import com.hahalolo.messenger.databinding.ChatDeleteOutcomingItemBinding
import com.hahalolo.messenger.databinding.ChatTextIncomingItemBinding
import com.hahalolo.messenger.databinding.ChatTextOutcomingItemBinding
import com.halo.data.entities.message.Message
import com.halo.data.room.entity.MessageEntity

abstract class ChatMessageRemovedViewHolder(
    binding: ViewDataBinding,
    chatListener: ChatListener
) : ChatMessageItemViewHolder(binding, chatListener) {

    private val messageText :TextView?  = itemView.findViewById(R.id.messageText) as? TextView

    override fun onBind(message: MessageEntity) {
        if(message.isRemoved()){
            messageText?.text = itemView.context.getString(R.string.chat_message_status_revoke)
        }else{
            messageText?.text = itemView.context.getString(R.string.chat_message_status_delete)
        }
    }

    class InComing(binding: ChatDeleteIncomingItemBinding, chatListener: ChatListener)
        : ChatMessageRemovedViewHolder(binding, chatListener)

    class OutComing(binding: ChatDeleteOutcomingItemBinding, chatListener: ChatListener)
        : ChatMessageRemovedViewHolder(binding, chatListener)
}