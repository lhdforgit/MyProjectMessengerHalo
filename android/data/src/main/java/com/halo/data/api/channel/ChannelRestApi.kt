/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.api.channel

import com.halo.data.entities.channel.Channel
import com.halo.data.entities.channel.ChannelBody
import com.halo.data.entities.channel.ChannelCreateResponse
import com.halo.data.entities.channel.ChannelFriendResponse
import com.halo.data.entities.response.ResponsePaging
import okhttp3.MultipartBody

/**
 * @author ndn
 * Created by ndn
 * Created on 8/21/18
 */
interface ChannelRestApi {
    suspend fun channels(
        token: String,
        workspaceId: String = "0",
        limit: Int,
        since: String?
    ): ResponsePaging<Channel>?

    suspend fun createChannel(
        token: String,
        workspaceId: String,
        name : MultipartBody.Part,
        avatar : MultipartBody.Part?
    ): ChannelCreateResponse?

    suspend fun channelById(
        token: String,
        workspaceId: String,
        channelId: String?
    ): Channel?

    suspend fun updateChannel(
        token: String,
        workspaceId: String,
        channelId: String?,
        body : ChannelBody?
    ): Channel?

    suspend fun updateChannel(
        token: String,
        workspaceId: String,
        channelId: String?,
        name : MultipartBody.Part,
        avatar : MultipartBody.Part
    ): Channel?

    suspend fun deleteChannel(
        token: String,
        workspaceId: String,
        channelId: String?,
    ): Channel?

    suspend fun findChannel(
        token:String,
        friendId: String?
    ): Channel?

    suspend fun createChannel(
        token:String,
        friendId: String?
    ): ChannelFriendResponse?

    suspend fun typing(
        token: String,
        workspaceId: String?,
        channelId: String?
    ): Any?
}
