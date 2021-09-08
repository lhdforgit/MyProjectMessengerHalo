/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.group.member

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.hahalolo.messager.MessengerController
import com.hahalolo.messager.presentation.base.AbsMessViewModel
import com.hahalolo.messager.presentation.group.detail.ChannelUiState
import com.hahalolo.messager.utils.Strings
import com.halo.data.common.resource.Resource
import com.halo.data.entities.channel.Channel
import com.halo.data.entities.member.LeaveChannelBody
import com.halo.data.entities.member.MemberChannel
import com.halo.data.entities.member.MemberRoleBody
import com.halo.data.entities.member.UpdateMemberBody
import com.halo.data.entities.role.RoleChannel
import com.halo.data.repository.channel.ChannelRepository
import com.halo.data.repository.member.MemberRepository
import com.halo.data.repository.role.RoleRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

import javax.inject.Inject

class ChatGroupMemberViewModel @Inject
internal constructor(
    private val memberRepository: MemberRepository,
    private val roleRepository: RoleRepository,
    private val channelRepository: ChannelRepository,
    appController: MessengerController,
) : AbsMessViewModel(appController) {

    var channelId = MutableLiveData<String>()

    var isShowOwner = true

    var memberLogin = MemberChannel()

    var roleChannel = mutableListOf<RoleChannel>()

    val isHasRole: Boolean
        get() = roleChannel.isNotEmpty()

    val adminRoleId: String
        get() {
            roleChannel.find { it.name.equals(MemberRole.ADMIN) }?.let {
                return it.roleId
            }
            return ""
        }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val memberPaging = flowOf(
        channelId.asFlow().flatMapLatest {
            memberRepository.memberPaging(token(), channelId = it)
        }
    ).flattenMerge(2)
        .cachedIn(viewModelScope)

    suspend fun getMemberLogin() {
        channelId.value?.let {
            memberRepository.memberDetail(
                token = token(),
                channelId = it,
                userId = userIdToken()
            ).collect {
                when {
                    it.isLoading -> {

                    }
                    it.isSuccess -> {
                        it.data?.let {
                            memberLogin = it
                        }
                    }
                    else -> {

                    }
                }
            }
        }
    }

    suspend fun getRoleChannel() {
        channelId.value?.let {
            roleRepository.getRoleChannel(
                token = token(),
                channelId = it
            ).collect {
                roleChannel = it
            }
        }
    }

    val memberLoginRole: String
        get() {
            return memberLogin.roles?.lastOrNull()?.name ?: MemberRole.MEMBER
        }

    val memberLoginIsOwner : Boolean
        get() = memberLogin.isOwner

    val memberLoginIsAdmin : Boolean
        get() = memberLogin.isAdmin

    val memberLoginIsMember : Boolean
        get() = memberLogin.isMember

    suspend fun refreshObserverMember() {
        channelId.apply {
            delay(100)
            postValue(value)
        }
    }

    suspend fun updateMemberNickname(
        userId: String,
        nickName: String
    ) {
        val body = UpdateMemberBody(nickName = nickName)
        memberRepository.updateMember(
            token = token(),
            channelId = channelId.value,
            userId = userId,
            body = body
        ).collectLatest {
            when {
                it.isLoading -> {

                }
                it.isSuccess -> {
                    it.data?.let {
                        refreshObserverMember()
                    }
                }
                else -> {

                }
            }
        }
    }

    suspend fun deleteMember(userId: String) {
        memberRepository.deleteMember(
            token = token(),
            channelId = channelId.value,
            userId = userId
        ).collectLatest {
            when {
                it.isLoading -> {
                }
                it.isSuccess -> {
                    it.data?.let {
                        refreshObserverMember()
                    }
                }
                else -> {

                }
            }
        }
    }

    suspend fun leaveChannel(newOwnerId: String?): Flow<Resource<MemberChannel>> {
        val body = LeaveChannelBody(newOwnerId)
        return memberRepository.leaveChannel(
            token = token(),
            channelId = channelId.value,
            body = body
        )
    }

    suspend fun setAdminMember(userId: String) {
        val body = MemberRoleBody(listOf(adminRoleId))
        channelId.value?.let {
            memberRepository.setRoleMember(
                token = token(),
                channelId = it,
                userId = userId,
                body = body
            ).collect {
                when {
                    it.isLoading -> {
                    }
                    it.isSuccess -> {
                        it.data?.let {
                            refreshObserverMember()
                        }
                    }
                    else -> {

                    }
                }
            }
        }
    }

    suspend fun deleteAdminMember(userId: String) {
        val body = MemberRoleBody(listOf(adminRoleId))
        channelId.value?.let {
            memberRepository.deleteRoleMember(
                token = token(),
                channelId = it,
                userId = userId,
                body = body
            ).collect {
                when {
                    it.isLoading -> {
                    }
                    it.isSuccess -> {
                        it.data?.let {
                            refreshObserverMember()
                        }
                    }
                    else -> {

                    }
                }
            }
        }
    }

    suspend fun deleteChannel(): Flow<Resource<Channel>>? {
        channelId.value?.let {
            return channelRepository.deleteChannel(
                token = token(),
                channelId = it
            )
        }
        return null
    }
}
