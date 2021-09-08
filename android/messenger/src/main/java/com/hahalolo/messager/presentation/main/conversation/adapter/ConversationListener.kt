package com.hahalolo.messager.presentation.main.conversation.adapter

import com.halo.data.room.entity.ChannelEntity


interface ConversationListener {

    fun onClick(channel: ChannelEntity)
    fun onClickMenuMore(channelEntity: ChannelEntity)
    fun onClickMenuRemove(channelEntity: ChannelEntity)
}