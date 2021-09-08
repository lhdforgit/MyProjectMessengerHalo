package com.halo.data.repository.member

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.halo.data.common.resource.Resource
import com.halo.data.entities.member.*
import com.halo.data.room.entity.MemberEntity
import kotlinx.coroutines.flow.Flow

interface MemberRepository {

    suspend fun memberPaging(
        token: String,
        workspaceId: String = "0",
        channelId: String?
    ): Flow<PagingData<MemberChannel>>

    suspend fun updateMember(
        token: String,
        workspaceId: String = "0",
        channelId: String?,
        userId: String,
        body: UpdateMemberBody
    ): Flow<Resource<MemberChannel>>

    suspend fun deleteMember(
        token: String,
        workspaceId: String = "0",
        channelId: String?,
        userId: String
    ): Flow<Resource<MemberChannel>>

    suspend fun leaveChannel(
        token: String,
        workspaceId: String = "0",
        channelId: String?,
        body: LeaveChannelBody
    ): Flow<Resource<MemberChannel>>

    suspend fun memberDetail(
        token: String,
        workspaceId: String = "0",
        channelId: String?,
        userId: String?
    ): Flow<Resource<MemberChannel>>

    suspend fun setRoleMember(
        token: String,
        workspaceId: String = "0",
        channelId: String,
        userId: String,
        body: MemberRoleBody
    ): Flow<Resource<MemberChannel>>

    suspend fun deleteRoleMember(
        token: String,
        workspaceId: String = "0",
        channelId: String,
        userId: String,
        body: MemberRoleBody
    ): Flow<Resource<MemberChannel>>

    fun getMemberList(
        channelId: String,
        userIds: MutableList<String>
    ): LiveData<MutableList<MemberEntity>>

    //from socket
    fun insertNewMember(member: Member)

}