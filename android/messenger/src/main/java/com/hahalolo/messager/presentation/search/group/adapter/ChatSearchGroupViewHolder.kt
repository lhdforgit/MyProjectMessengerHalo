/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.search.group.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.hahalolo.messager.chatkit.commons.ImageLoader
import com.hahalolo.messager.presentation.adapter.preload.PreloadHolder
import com.halo.data.room.entity.ChannelEntity
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatMessageSearchGroupItemBinding
import java.util.*

/**
 * @author BuiSon
 * Created by BuiSon
 * Created on 10/23/2018.
 */
class ChatSearchGroupViewHolder private constructor(private val binding: ChatMessageSearchGroupItemBinding,
                                                    private val imageLoader: ImageLoader,
                                                    private val listener: ChatSearchGroupListener
)
    : RecyclerView.ViewHolder(binding.root), PreloadHolder {

    fun bind(entity: ChannelEntity) {
        binding.conversationNameTv.text = entity.channelName()
//        binding.conversationMessTv.text = String.format("Nhom %s thanh vien", entity.getListMembers().size)
        binding.avatarIv.setRequestManager(imageLoader.getRequestManager())
        binding.avatarIv.setImage(entity.channelAvatar())
        itemView.setOnClickListener {listener.messageRoomChat(entity.channelId()) }
        binding.executePendingBindings()
    }

    override fun getTargets(): MutableList<View> {
        val views = mutableListOf<View>()
        views.add(itemView)
        return views
    }

    override fun invalidateLayout(requestManager: RequestManager?) {
        requestManager?.clear(itemView)
        binding.avatarIv.invalidateLayout(requestManager)
    }

    companion object {

        fun getHolder(parent: ViewGroup,
                      imageLoader: ImageLoader,
                      listener: ChatSearchGroupListener
        ): ChatSearchGroupViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = DataBindingUtil.inflate<ChatMessageSearchGroupItemBinding>(inflater,
                    R.layout.chat_message_search_group_item,
                    parent, false)
            return ChatSearchGroupViewHolder(
                binding,
                imageLoader,
                listener
            )
        }
    }
}