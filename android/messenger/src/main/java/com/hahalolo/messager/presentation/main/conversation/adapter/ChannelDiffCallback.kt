package com.hahalolo.messager.presentation.main.conversation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.halo.data.entities.channel.Channel

class ChannelDiffCallback : DiffUtil.ItemCallback<Channel>() {

    override fun areItemsTheSame(oldItem: Channel, newItem: Channel): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItem: Channel, newItem: Channel): Boolean {
        return false
    }
}