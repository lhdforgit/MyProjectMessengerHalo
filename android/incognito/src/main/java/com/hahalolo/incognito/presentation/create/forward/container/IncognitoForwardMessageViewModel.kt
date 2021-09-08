package com.hahalolo.incognito.presentation.create.forward.container

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.paging.PagingData
import com.halo.data.entities.channel.Channel
import com.halo.data.entities.contact.Contact
import com.halo.data.repository.channel.ChannelRepository
import com.halo.data.repository.contact.ContactRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class IncognitoForwardMessageViewModel
@Inject constructor(
    private val channelRepository: ChannelRepository,
    private val contactRepository: ContactRepository
) : ViewModel() {

    val token =
        "v2.public.eyJkZXYiOiJkY2RhZDgzYTY4NDBiMjc2Iiwic3ViIjoiMTk3OTM4OTQzNjE2ODc2NTQ0IiwiaXNzIjoiaGFoYWxvbG8iLCJpYXQiOiIyMDIxLTA2LTMwVDA2OjEwOjU2LjQzM1oiLCJleHAiOiIyMDIyLTA2LTMwVDA2OjEwOjU2LjQzM1oifbNrwea-lCzKLi9al5_xboU1yzXplb2uf5gwzfbxIjiwfqx1drEtDXF8mKoMt1k67ww_qrlkvIDPPu5KNvTxCAA"

    suspend fun listContact(): Flow<PagingData<Contact>> {
        return contactRepository.contactPaging(token)
    }

    suspend fun listChannel(): Flow<PagingData<Channel>> {
        return channelRepository.channelPaging(token = token)
    }
}