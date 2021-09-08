/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.api.channel

import com.halo.data.api.utils.ServiceGenerator
import com.halo.data.entities.channel.Channel
import com.halo.data.entities.channel.ChannelBody
import com.halo.data.entities.response.ResponsePaging
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author ndn
 * Created by ndn
 * Created on 9/6/18
 */
@Singleton
class ChannelRestApiImpl @Inject
internal constructor() : ChannelRestApi {

    override suspend fun channels(
        token: String,
        workspaceId: String,
        limit: Int,
        since: String?
    ): ResponsePaging<Channel>? = ServiceGenerator.createMessService(
        serviceClass = ChannelService::class.java,
        authorization = token
    ).channels(workspaceId, limit, since)


    override suspend fun createChannel(
        token: String,
        workspaceId: String,
        name: MultipartBody.Part,
        avatar: MultipartBody.Part?
    ) = ServiceGenerator.createMessService(
        serviceClass = ChannelService::class.java,
        authorization = token
    ).createChannel(workspaceId, name, avatar)

    override suspend fun channelById(
        token: String,
        workspaceId: String,
        channelId: String?
    ) = ServiceGenerator.createMessService(
        serviceClass = ChannelService::class.java,
        authorization = token
    ).channelById(workspaceId, channelId)

    override suspend fun updateChannel(
        token: String,
        workspaceId: String,
        channelId: String?,
        body: ChannelBody?
    ) = ServiceGenerator.createMessService(
        serviceClass = ChannelService::class.java,
        authorization = token
    ).updateChannel(workspaceId, channelId, body)

    override suspend fun updateChannel(
        token: String,
        workspaceId: String,
        channelId: String?,
        name: MultipartBody.Part,
        avatar: MultipartBody.Part
    ) = ServiceGenerator.createMessService(
        serviceClass = ChannelService::class.java,
        authorization = token
    ).updateChannel(workspaceId, channelId, name, avatar)

    override suspend fun deleteChannel(
        token: String,
        workspaceId: String,
        channelId: String?
    ) = ServiceGenerator.createMessService(
        serviceClass = ChannelService::class.java,
        authorization = token
    ).deleteChannel(workspaceId, channelId)

    override suspend fun findChannel(
        token: String, friendId: String?
    ) = ServiceGenerator.createMessService(
        serviceClass = ChannelService::class.java,
        authorization = token
    ).findChannel(friendId)

    override suspend fun createChannel(
        token: String,
        friendId: String?
    ) = ServiceGenerator.createMessService(
        serviceClass = ChannelService::class.java,
        authorization = token
    ).createChannel(friendId)

    override suspend fun typing(
        token: String,
        workspaceId: String?,
        channelId: String?
    ) = ServiceGenerator.createMessService(
        serviceClass = ChannelService::class.java,
        authorization = token
    ).typing(workspaceId, channelId)
}