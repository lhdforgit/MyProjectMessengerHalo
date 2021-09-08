package com.hahalolo.incognito.presentation.setting.invite

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.halo.data.entities.contact.Contact
import com.halo.data.repository.channel.ChannelRepository
import com.halo.data.repository.contact.ContactRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IncognitoInviteFriendViewModel
@Inject constructor(
    private val channelRepository: ChannelRepository,
    private val contactRepository: ContactRepository,
) : ViewModel() {

    val token =
        "v2.public.eyJkZXYiOiJkY2RhZDgzYTY4NDBiMjc2Iiwic3ViIjoiMTk0MjcwMzM3MjIxNzg3NjQ4IiwiaXNzIjoiaGFoYWxvbG8iLCJpYXQiOiIyMDIxLTA2LTIyVDAyOjU1OjU0LjI4MloiLCJleHAiOiIyMDIyLTA2LTIyVDAyOjU1OjU0LjI4MloifYVpdd9fx4MeqGIRzGgFX3h3DeZ3uzz-OXVY1xSxxRW2sOwoXModCvtNlwzunVYjEpizhW52-zsIgUVEv5tsHQI"

    var listId = mutableListOf<String>()

    suspend fun listContact(): Flow<PagingData<Contact>> {
        return contactRepository.contactPaging(token)
    }
}