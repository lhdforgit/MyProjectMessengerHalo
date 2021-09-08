package com.hahalolo.messager.presentation.forward_message.container

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.PagingData
import com.hahalolo.messager.MessengerController
import com.hahalolo.messager.presentation.base.AbsMessViewModel
import com.halo.data.entities.channel.Channel
import com.halo.data.repository.channel.ChannelPaging
import com.halo.data.repository.channel.ChannelRepository
import com.halo.data.room.entity.ChannelEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ForwardMessageViewModel @Inject constructor(
    appController: MessengerController,
   val channelRepository: ChannelRepository,
) :
    AbsMessViewModel(appController) {
    suspend fun listChannel(): Flow<PagingData<Channel>> {
        return channelRepository.channelPaging(token = token())
    }
}