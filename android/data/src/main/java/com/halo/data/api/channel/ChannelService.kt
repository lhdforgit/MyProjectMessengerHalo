/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.api.channel

import com.halo.data.entities.channel.*
import com.halo.data.entities.response.ResponsePaging
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * @author ngannd
 * Create by ngannd on 24/05/2019
 */
interface ChannelService {

    @GET("/v2/workspaces/{workspaceId}/channels")
    suspend fun channels(
        @Path("workspaceId") workspaceId: String,
        @Query("limit") limit: Int,
        @Query("since") since: String?=null
    ): ResponsePaging<Channel>?

    @Multipart
    @POST("/v2/workspaces/{workspaceId}/channels")
    suspend fun createChannel(
        @Path("workspaceId") workspaceId: String?,
        @Part name : MultipartBody.Part,
        @Part avatar : MultipartBody.Part?
    ): ChannelCreateResponse?

    @GET("/v2/workspaces/{workspaceId}/channels/{channelId}")
    suspend fun channelById(
        @Path("workspaceId") workspaceId : String?,
        @Path("channelId") channelId: String?
    ): Channel?

    @PUT("/v2/workspaces/{workspaceId}/channels/{channelId}")
    suspend fun updateChannel(
        @Path("workspaceId") workspaceId : String?,
        @Path("channelId") channelId: String?,
        @Body body : ChannelBody?
    ) : Channel?

    @Multipart
    @PUT("/v2/workspaces/{workspaceId}/channels/{channelId}")
    suspend fun updateChannel(
        @Path("workspaceId") workspaceId : String?,
        @Path("channelId") channelId: String?,
        @Part name : MultipartBody.Part,
        @Part avatar : MultipartBody.Part
    ) : Channel?

    @DELETE("/v2/workspaces/{workspaceId}/channels/{channelId}")
    suspend fun deleteChannel(
        @Path("workspaceId") workspaceId : String?,
        @Path("channelId") channelId: String?,
    ) : Channel?

    @GET("/v2/dms/{recipientId}")
    suspend fun findChannel(
        @Path("recipientId") friendId: String?
    ): Channel?

    @POST("/v2/dms/{recipientId}")
    suspend fun createChannel(
        @Path("recipientId") friendId: String?
    ): ChannelFriendResponse?

    @POST("v2/workspaces/{workspaceId}/channels/{channelId}/type")
    suspend fun typing(
        @Path("workspaceId") workspaceId : String?,
        @Path("channelId") channelId: String?
    ): Any?


}
