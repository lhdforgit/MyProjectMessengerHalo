/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.main.conversation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.hahalolo.messager.MessengerController
import com.hahalolo.messager.presentation.base.AbsMessViewModel
import com.halo.data.repository.channel.ChannelPaging
import com.halo.data.repository.channel.ChannelRepository
import com.halo.data.room.entity.ChannelEntity
import javax.inject.Inject

/**
 * @author BuiSon
 * Created by BuiSon
 * Created on 10/23/2018.
 */
class ConversationListViewModel @Inject
internal constructor(
    channelRepository: ChannelRepository,
    appController: MessengerController,
) : AbsMessViewModel(appController) {

    val token = MutableLiveData<String>(token())

    val channelPaging : LiveData<ChannelPaging> = Transformations.switchMap(token) {
        channelRepository.channelPaging2(
            it?:"",
            workspaceId = "0",
            ownerId = appController.ownerId
        )
    }

    val channelState = Transformations.switchMap(channelPaging){
        it.networkState
    }

    val channels : LiveData<MutableList<ChannelEntity>> = Transformations.switchMap(channelPaging){
        it.listData
    }

    fun refresh() {
        channelPaging.value?.refresh()
    }
}
