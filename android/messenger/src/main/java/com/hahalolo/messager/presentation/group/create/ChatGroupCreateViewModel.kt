/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.group.create

import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.paging.PagingData
import com.google.common.collect.Iterators
import com.hahalolo.messager.MessengerController
import com.halo.data.room.entity.MemberEntity
import com.hahalolo.messager.bubble.conversation.detail.friend.select.adapter.FriendSelectData
import com.hahalolo.messager.presentation.base.AbsMessViewModel
import com.halo.data.repository.channel.ChannelRepository
import com.halo.data.entities.channel.ChannelBody
import com.halo.data.entities.contact.Contact
import com.halo.data.repository.contact.ContactRepository
import com.halo.data.repository.user.UserRepository
import com.halo.data.worker.channel.CreateChannelWorker
import com.halo.data.worker.channel.InviteJoinChannelWorker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import java.util.*
import javax.inject.Inject

class ChatGroupCreateViewModel @Inject constructor(
    private val contactRepository: ContactRepository,
    private val userRepository: UserRepository,
    appController: MessengerController,
) : AbsMessViewModel(appController) {

    var listFriendChoose = MutableLiveData<MutableList<FriendSelectData>>()

    val listMembers = MutableLiveData<MutableList<MemberEntity>>()

    var channelId = ""

    fun isChecked(userId: String): Boolean {
        return indexChecked(userId) >= 0
    }

    private fun indexChecked(userId: String): Int {
        val currentList = listFriendChoose.value ?: mutableListOf()
        return Iterators.indexOf(currentList.iterator()) {
            TextUtils.equals(it?.userId, userId)
        }
    }

    fun isGroup(): Boolean {
        if (listFriendChoose.value?.size ?: 0 >= 1) return true
        return false
    }

    fun addContactSelected(data: FriendSelectData) {
        val list: MutableList<FriendSelectData> = listFriendChoose.value
            ?: mutableListOf()
        val iterator = list.iterator()
        val index = Iterators.indexOf(iterator) { input ->
            input?.run { TextUtils.equals(input.userId, data.userId) }
                ?: false
        }
        if (index < 0) {
            list.add(data)
            listFriendChoose.postValue(list)
        }
    }

    fun removeContactSelected(data: FriendSelectData) {
        listFriendChoose.value?.takeIf { it.isNotEmpty() }?.let {
            val iterator = it.iterator()
            Iterators.removeIf(iterator) { input ->
                input != null && TextUtils.equals(
                    input.userId,
                    data.userId
                )
            }
            listFriendChoose.postValue(listFriendChoose.value)
        }
    }

    fun listContactId(): MutableList<String> {
        val result = mutableListOf<String>()
        listFriendChoose.value?.forEach {
            if (!TextUtils.equals(it.userId, appController.ownerId)) {
                result.add(it.userId ?: "")
            }
        }
        return result
    }

    fun isMember(friendId: String?): Boolean {
        listMembers.value?.takeIf { it.isNotEmpty() }?.run {
            return Iterators.indexOf(this.iterator()) {
                TextUtils.equals(it?.userId(), friendId)
            } >= 0
        }
        return false
    }

    suspend fun createChannel(context: Context, body: ChannelBody): UUID {
        return CreateChannelWorker.createChannel(
            context = context,
            token = token(),
            workspaceId = "0",
            channelBody = body
        )
    }

    suspend fun inviteJoinChannel(context: Context): UUID {
        val userIds = listContactId().toTypedArray()
        return InviteJoinChannelWorker.inviteChannel(
            context = context,
            token = token(),
            workspaceId = "0",
            channelId = channelId,
            userIds = userIds
        )
    }

    suspend fun listContact(): Flow<PagingData<Contact>> {
        return contactRepository.contactPaging(token())
    }

    val keywordData = MutableLiveData<String>()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val userSearch = flowOf(
        keywordData.asFlow().flatMapLatest { keyword ->
            userRepository.searchUser(
                token = token(),
                keyword = keyword
            )
        }
    ).flattenMerge(2)
}