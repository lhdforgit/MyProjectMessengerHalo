/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.holder.status

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatNetworkStatusItemBinding

class ChatNetworkStatusItem(val binding: ChatNetworkStatusItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindStatus() {

    }

    companion object {
        fun create(parent: ViewGroup): ChatNetworkStatusItem {

            val binding = DataBindingUtil.inflate<ChatNetworkStatusItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.chat_network_status_item,
                parent,
                false
            )
            return ChatNetworkStatusItem(binding)
        }
    }
}