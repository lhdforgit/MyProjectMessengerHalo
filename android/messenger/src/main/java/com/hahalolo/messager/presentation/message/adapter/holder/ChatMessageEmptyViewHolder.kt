package com.hahalolo.messager.presentation.message.adapter.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.hahalolo.messager.chatkit.adapter.ChatListener
import com.hahalolo.messager.presentation.message.adapter.ChatMessageModel
import com.hahalolo.messenger.databinding.ChatEmptyHolderItemBinding

class ChatMessageEmptyViewHolder (binding: ChatEmptyHolderItemBinding, chatListener: ChatListener)
    : ChatMessageViewHolder(binding.root, chatListener){
    override fun onBind(message: ChatMessageModel) {

    }

}