/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.group.detail

import androidx.lifecycle.MutableLiveData
import com.hahalolo.cache.setting.SettingPref
import com.hahalolo.messager.MessengerController
import com.hahalolo.messager.presentation.base.AbsMessViewModel
import com.hahalolo.messager.presentation.group.gallery.adapter.MessengerManagerFileModel
import com.hahalolo.messager.utils.Strings
import com.halo.data.common.paging.Listing
import com.halo.data.entities.channel.Channel
import com.halo.data.entities.channel.ChannelBody
import com.halo.data.entities.member.LeaveChannelBody
import com.halo.data.repository.attachment.AttachmentRepository
import com.halo.data.repository.channel.ChannelRepository
import com.halo.data.repository.member.MemberRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

class ChatGroupDetailViewModel @Inject constructor(
    private val channelRepository: ChannelRepository,
    private val attachmentRepository: AttachmentRepository,
    private val memberRepository: MemberRepository,
    private val settingPref: SettingPref,
    appController: MessengerController,
) : AbsMessViewModel(appController) {

    private val _uiState = MutableStateFlow<ChannelUiState>(ChannelUiState.Nothing(null))
    val uiState: MutableStateFlow<ChannelUiState> = _uiState

    var channelIdLive = MutableLiveData<String>()

    var channelId = ""

    var channelResponse = MutableLiveData<Channel>()

    private fun setChannel(channel: Channel) {
        channelResponse.value = channel
    }

    private val channelEntity: Channel?
        get() = channelResponse.value

    val channelName: String?
        get() = channelEntity?.channelName()

    val avatarChannel: String
        get() = channelEntity?.avatar ?: ""

    val isHaveAvatar: Boolean
        get() = avatarChannel.isNotEmpty()

    val isOpenBubble: Boolean
        get() = settingPref.isBubbleOpen()

    suspend fun channelDetail() {
        channelRepository.getChannelDetail(
            token = token(),
            channelId = channelId
        ).collect {
            when {
                it.isLoading -> {

                }
                it.isSuccess -> {
                    it.data?.run {
                        setChannel(this)
                    }
                }
                else -> {
                    uiState.value = ChannelUiState.Error(null)
                }
            }
        }
    }

    suspend fun updateChannelName(name: String) {
        val body = ChannelBody(name = name)
        channelRepository.updateChannel(
            token = token(),
            channelId = channelId,
            body = body
        ).collect {
            when {
                it.isLoading -> {

                }
                it.isSuccess -> {
                    it.data?.run {
                        setChannel(this)
                    }
                }
                else -> {
                    uiState.value = ChannelUiState.Error(null)
                }
            }
        }
    }

    suspend fun updateChannelAvatar(avatar: String) {
        val body = ChannelBody(name = channelName, avatar = avatar)
        channelRepository.updateChannel(
            token = token(),
            channelId = channelId,
            body = body
        ).collect {
            when {
                it.isLoading -> {

                }
                it.isSuccess -> {
                    it.data?.run {
                        setChannel(this)
                    }
                }
                else -> {
                    uiState.value = ChannelUiState.Error(null)
                }
            }
        }
    }

    suspend fun deleteChannel() {
        channelRepository.deleteChannel(
            token = token(),
            channelId = channelId
        ).collect {
            when {
                it.isLoading -> {

                }
                it.isSuccess -> {
                    it.data?.run {
                        uiState.value = ChannelUiState.ChannelDelete(this)
                    }
                }
                else -> {
                    uiState.value = ChannelUiState.Error(null)
                }
            }
        }
    }

    suspend fun getAttachments() {
        attachmentRepository.getAttachments(
            token = token(),
            workspaceId = "0",
            channelId = channelId,
            type = "image"
        )?.collect {
            when {
                it.isLoading -> {

                }
                it.isSuccess -> {
                    it.data?.run {
                        this.map { MessengerManagerFileModel.convertToMediaModel(it) }
                            .toMutableList()
                            .let {
                                uiState.value = ChannelUiState.ChannelMedia(it)
                            }
                    }
                }
                else -> {

                }
            }
        }
    }

    suspend fun leaveChannel(newOwnerId : String?) {
        val body = LeaveChannelBody(newOwnerId)
        memberRepository.leaveChannel(
            token = token(),
            workspaceId = "0",
            channelId = channelId,
            body = body
        ).collect {
            when {
                it.isLoading -> {

                }
                it.isSuccess -> {
                    it.data?.run {
                        uiState.value = ChannelUiState.ChannelLeave(this.channelId)
                    }
                }
                else -> {
                    uiState.value = ChannelUiState.Error(null)
                }
            }
        }
    }
}

sealed class ChannelUiState {
    data class Nothing(val message: String?) : ChannelUiState()
    data class Loading(val message: String?) : ChannelUiState()
    data class Error(val message: String?) : ChannelUiState()
    data class Success(val message: String?) : ChannelUiState()
    data class ChannelDetail(val data: Channel?) : ChannelUiState()
    data class ChannelDelete(val data: Channel?) : ChannelUiState()
    data class ChannelMedia(val data: MutableList<MessengerManagerFileModel>?) : ChannelUiState()
    data class ChannelLeave(val message: String?) : ChannelUiState()
}