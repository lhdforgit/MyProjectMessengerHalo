package com.hahalolo.incognito.presentation.create.channel

import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.halo.common.utils.list.ListUtils
import com.halo.data.common.resource.Resource
import com.halo.data.entities.channel.ChannelBody
import com.halo.data.entities.channel.ChannelCreateResponse
import com.halo.data.entities.contact.Contact
import com.halo.data.repository.attachment.AttachmentRepository
import com.halo.data.repository.channel.ChannelRepository
import com.halo.data.repository.contact.ContactRepository
import com.halo.data.worker.channel.CreateChannelWorker
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject

/**
 * Create by ndn
 * Create on 5/26/21
 * com.hahalolo.incognito.presentation.main
 */
class IncognitoCreateChannelViewModel
@Inject constructor(
    private val channelRepository: ChannelRepository,
    private val contactRepository: ContactRepository,
    private val attachmentRepository: AttachmentRepository
) : ViewModel() {
    val token =
        "v2.public.eyJkZXYiOiJkY2RhZDgzYTY4NDBiMjc2Iiwic3ViIjoiMTk3OTM4OTQzNjE2ODc2NTQ0IiwiaXNzIjoiaGFoYWxvbG8iLCJpYXQiOiIyMDIxLTA3LTAxVDEwOjE0OjUwLjU4M1oiLCJleHAiOiIyMDIyLTA3LTAxVDEwOjE0OjUwLjU4M1oifXczFZBBJDrKp0wU_q3jBW34e3JDjw5mU9w7rjW-3ltFCKgWF9XwoLbwSc3tkINvM7E6l-sWBngxGSnzFDqN6wM"

    var listId = mutableListOf<String>()

    fun onSelected(id: String) {
        ListUtils.isDataExits(listId) { input -> TextUtils.equals(input, id) }.takeUnless { it }
            ?.run {
                listId.add(id)
            }
    }

    fun onUnSelect(id: String) {
        ListUtils.isDataExits(listId) { input -> TextUtils.equals(input, id) }.takeIf { it }
            ?.run {
                listId.remove(id)
            }
    }

    suspend fun listContact(): Flow<PagingData<Contact>> {
        return contactRepository.contactPaging(token)
    }

    suspend fun createChannel(body: ChannelBody): Flow<Resource<ChannelCreateResponse>> {
        return channelRepository.createChannel(token = token, body = body)
    }

    suspend fun createChannel(context: Context, body: ChannelBody): UUID {
        return CreateChannelWorker.createChannel(
            context = context,
            token = token,
            workspaceId = "0",
            channelBody = body
        )
    }
}