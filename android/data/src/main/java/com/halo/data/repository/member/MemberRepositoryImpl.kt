package com.halo.data.repository.member

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.halo.data.api.member.MemberRestApi
import com.halo.data.common.resource.Resource
import com.halo.data.entities.channel.ChannelCreateResponse
import com.halo.data.entities.member.*
import com.halo.data.room.dao.MemberDao
import com.halo.data.room.entity.MemberEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemberRepositoryImpl @Inject constructor(
    private val memberRestApi: MemberRestApi,
    private val memberDao: MemberDao

) : MemberRepository {

    override suspend fun memberPaging(
        token: String,
        workspaceId: String,
        channelId: String?
    ): Flow<PagingData<MemberChannel>> =
        Pager(PagingConfig(pageSize = 20)) {
            MemberChannelPagingSource(
                api = memberRestApi,
                token = token,
                workspaceId = workspaceId,
                channelId = channelId
            )
        }.flow

    override suspend fun updateMember(
        token: String,
        workspaceId: String,
        channelId: String?,
        userId: String,
        body: UpdateMemberBody
    ): Flow<Resource<MemberChannel>> = flow {
        emit(Resource.loading<MemberChannel>(null))
        val resource = kotlin.runCatching {
            memberRestApi.updateMember(
                token = token,
                workspaceId = workspaceId,
                channelId = channelId,
                userId = userId,
                body = body
            )?.run {
                Resource.success(this)
            } ?: kotlin.run {
                Resource.error<MemberChannel>(500, "errors", null)
            }
        }.getOrElse {
            Resource.error(it)
        }
        emit(resource)
    }.catch {
        Resource.error<MemberChannel>(it)
    }.flowOn(Dispatchers.IO)

    override suspend fun deleteMember(
        token: String,
        workspaceId: String,
        channelId: String?,
        userId: String
    ): Flow<Resource<MemberChannel>> = flow {
        emit(Resource.loading<MemberChannel>(null))
        val resource = kotlin.runCatching {
            memberRestApi.deleteMember(
                token = token,
                workspaceId = workspaceId,
                channelId = channelId,
                userId = userId
            )?.run {
                Resource.success(this)
            } ?: kotlin.run {
                Resource.error<MemberChannel>(500, "errors", null)
            }
        }.getOrElse {
            Resource.error(it)
        }
        emit(resource)
    }.catch {
        Resource.error<MemberChannel>(it)
    }.flowOn(Dispatchers.IO)

    override suspend fun leaveChannel(
        token: String,
        workspaceId: String,
        channelId: String?,
        body: LeaveChannelBody
    ): Flow<Resource<MemberChannel>> = flow {
        emit(Resource.loading<MemberChannel>(null))
        val resource = kotlin.runCatching {
            memberRestApi.leaveChannel(
                token = token,
                workspaceId = workspaceId,
                channelId = channelId,
                body = body
            )?.run {
                Resource.success(this)
            } ?: kotlin.run {
                Resource.error<MemberChannel>(500, "errors", null)
            }
        }.getOrElse {
            Resource.error(it)
        }
        emit(resource)
    }.catch {
        Resource.error<MemberChannel>(it)
    }.flowOn(Dispatchers.IO)

    override suspend fun memberDetail(
        token: String,
        workspaceId: String,
        channelId: String?,
        userId: String?
    ): Flow<Resource<MemberChannel>> = flow {
        emit(Resource.loading<MemberChannel>(null))
        val resource = kotlin.runCatching {
            memberRestApi.memberDetail(
                token = token,
                workspaceId = workspaceId,
                channelId = channelId,
                userId = userId
            )?.run {
                Resource.success(this)
            } ?: kotlin.run {
                Resource.error<MemberChannel>(500, "errors", null)
            }
        }.getOrElse {
            Resource.error(it)
        }
        emit(resource)
    }.catch {
        Resource.error<MemberChannel>(it)
    }.flowOn(Dispatchers.IO)

    override suspend fun setRoleMember(
        token: String,
        workspaceId: String,
        channelId: String,
        userId: String,
        body: MemberRoleBody
    ): Flow<Resource<MemberChannel>> = flow {
        emit(Resource.loading<MemberChannel>(null))
        val resource = kotlin.runCatching {
            memberRestApi.setRoleMember(
                token = token,
                workspaceId = workspaceId,
                channelId = channelId,
                userId = userId,
                body = body
            )?.run {
                Resource.success(this)
            } ?: kotlin.run {
                Resource.error<MemberChannel>(500, "errors", null)
            }
        }.getOrElse {
            Resource.error(it)
        }
        emit(resource)
    }.catch {
        Resource.error<MemberChannel>(it)
    }.flowOn(Dispatchers.IO)

    override suspend fun deleteRoleMember(
        token: String,
        workspaceId: String,
        channelId: String,
        userId: String,
        body: MemberRoleBody
    ): Flow<Resource<MemberChannel>> = flow {
        emit(Resource.loading<MemberChannel>(null))
        val resource = kotlin.runCatching {
            memberRestApi.deleteRoleMember(
                token = token,
                workspaceId = workspaceId,
                channelId = channelId,
                userId = userId,
                body = body
            )?.run {
                Resource.success(this)
            } ?: kotlin.run {
                Resource.error<MemberChannel>(500, "errors", null)
            }
        }.getOrElse {
            Resource.error(it)
        }
        emit(resource)
    }.catch {
        Resource.error<MemberChannel>(it)
    }.flowOn(Dispatchers.IO)


    override fun getMemberList(
        channelId: String,
        userIds: MutableList<String>
    ): LiveData<MutableList<MemberEntity>> {
        return memberDao.getMemberList(channelId, userIds)
    }

    //TODO update from socket
    override fun insertNewMember(member: Member) {

    }
}