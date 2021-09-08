/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.holder.create_group

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.hahalolo.messager.chatkit.adapter.ChatListener
import com.hahalolo.messager.chatkit.commons.ImageLoader
import com.hahalolo.messager.chatkit.holder.view.ChatMessageCreateGroupListener
import com.hahalolo.messager.chatkit.messages.MessagesListStyle
import com.halo.data.room.entity.ChannelEntity
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatCreateGroupHolderItemBinding

class ChatCreateGroupHolderItem(
    var binding: ChatCreateGroupHolderItemBinding,
    var chatListener: ChatListener,
    var imageLoader: ImageLoader,
    var style: MessagesListStyle
) : RecyclerView.ViewHolder(binding.root) {

    private var observer : Observer<ChannelEntity>?=null

    companion object {
        fun create(
            parent: ViewGroup,
            chatListener: ChatListener,
            imageLoader: ImageLoader,
            style: MessagesListStyle
        ): ChatCreateGroupHolderItem {
            val binding = DataBindingUtil.inflate<ChatCreateGroupHolderItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.chat_create_group_holder_item,
                parent,
                false
            )
            return ChatCreateGroupHolderItem(binding!!, chatListener, imageLoader, style)
        }
    }

    fun onBind() {

        observer = Observer {
            binding.chatMsgCreateGrItem.updateMessageCreateGroup(
                it ,
                chatListener.ownerId(),
                imageLoader.getRequestManager(),
                object : ChatMessageCreateGroupListener {
                    override fun onClickAddMember(roomId: String) {
                        chatListener.onAddMemberInGroup()
                    }
                })
        }

        chatListener.observerRoomEntity(observer!!)
    }

    fun onRecycled(){
        observer?.run {
            chatListener.removeObserverRoomEntity(this)
        }
    }
}