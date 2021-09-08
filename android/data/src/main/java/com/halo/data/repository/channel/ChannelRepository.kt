package com.halo.data.repository.channel

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.halo.data.common.resource.Resource
import com.halo.data.entities.channel.*
import com.halo.data.entities.member.MemberChannel
import com.halo.data.entities.message.Message
import com.halo.data.repository.message.MessagePaging
import com.halo.data.room.entity.ChannelDetailEntity
import com.halo.data.room.entity.ChannelEntity
import com.halo.data.room.entity.MemberEntity
import kotlinx.coroutines.flow.Flow
import retrofit2.http.POST
import retrofit2.http.Path

interface ChannelRepository {
    suspend fun channelPaging(
        token: String? = null,
        workspaceId: String = "0"
    ): Flow<PagingData<Channel>>

    fun channelPaging2(
        token: String? = null,
        workspaceId: String = "0",
        ownerId: String
    ): LiveData<ChannelPaging>

    suspend fun createChannel(
        token: String,
        workspaceId: String = "0",
        body: ChannelBody
    ): Flow<Resource<ChannelCreateResponse>>

    suspend fun getChannelDetail(
        token: String,
        workspaceId: String = "0",
        channelId: String?
    ) : Flow<Resource<Channel>>

    suspend fun getChannelEntity(
        token: String,
        workspaceId: String = "0",
        channelId: String?
    ) : Flow<ChannelEntity?>

    fun getChannelDetailEntity(
        token: String,
        workspaceId: String = "0",
        channelId: String?,
    ) : LiveData<ChannelDetailEntity>

    suspend fun updateChannel(
        token: String,
        workspaceId: String = "0",
        channelId: String?,
        body : ChannelBody?
    ): Flow<Resource<Channel>>

    suspend fun deleteChannel(
        token: String,
        workspaceId: String = "0",
        channelId: String?,
    ): Flow<Resource<Channel>>

    suspend fun findChannel(
        token:String,
        workspaceId:String ="0",
        friendId: String?
    ): Flow<Resource<Channel>>

    suspend fun createChannel(
        token:String,
        workspaceId:String ="0",
        friendId: String?
    ): Flow<Resource<ChannelFriendResponse>>

    //from socket
    fun onNewChannel(token:String, channel: Channel)

    suspend fun typing(
        token:String,
        workspaceId : String?="0",
        channelId: String?
    ): Flow<Resource<Any>>

    fun onUserTyping(workspaceId: String, channelId: String?, userId: String?)

    fun mentionQuery(workspaceId: String = "0",
                     channelId: String?,
                     ownerId:String,
                     query:String
                     ): LiveData<MutableList<MemberEntity>>
}