/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.holder.empty

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatEmptyHolderItemBinding

class ChatEmptyViewHolder(binding: ChatEmptyHolderItemBinding)
    : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun create(parent: ViewGroup): ChatEmptyViewHolder {
            val binding = DataBindingUtil.inflate<ChatEmptyHolderItemBinding>(LayoutInflater.from(parent.context),
                    R.layout.chat_empty_holder_item,
                    parent,
                    false)
            return ChatEmptyViewHolder(binding!!)
        }
    }
}