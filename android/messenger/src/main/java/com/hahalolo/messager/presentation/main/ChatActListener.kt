package com.hahalolo.messager.presentation.main

import com.halo.data.room.entity.ChannelEntity

interface ChatActListener {

    fun onClickMenuMore(room: ChannelEntity)

    fun onClickMenuRemove(room: ChannelEntity)

}