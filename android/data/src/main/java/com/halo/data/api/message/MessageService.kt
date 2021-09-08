/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.api.message

import com.halo.data.entities.attachment.AttachmentResponse
import com.halo.data.entities.channel.Channel
import com.halo.data.entities.message.MessageSendBody
import com.halo.data.entities.message.Message
import com.halo.data.entities.response.ResponsePaging
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * @author ngannd
 * Create by ngannd on 24/05/2019
 */
interface MessageService {
    @GET("/v2/workspaces/{workspaceId}/channels/{channelId}/messages")
    suspend fun messages(
        @Path("workspaceId") workspaceId: String,
        @Path("channelId") channelId: String,
        @Query("limit") limit: Int,
        @Query("since") since: String?=null
    ): ResponsePaging<Message>?

    @PUT("/v2/workspaces/{workspaceId}/channels/{channelId}/messages/{messageId}")
    suspend fun editMessage(
        @Path("workspaceId") workspaceId: String,
        @Path("channelId") channelId: String,
        @Path("messageId") messageId: String,
        @Body body: MessageSendBody.TextBody
    ): Message?

    @POST("/v2/workspaces/{workspaceId}/channels/{channelId}/messages")
    suspend fun createMessage(
        @Path("workspaceId") workspaceId: String,
        @Path("channelId") channelId: String,
        @Body body: MessageSendBody.TextBody
    ): Message?

    @POST("/v2/workspaces/{workspaceId}/channels/{channelId}/messages/{messageId}/quote")
    suspend fun createMessageQuote(
        @Path("workspaceId") workspaceId: String,
        @Path("channelId") channelId: String,
        @Path("messageId") messageId: String,
        @Body body: MessageSendBody.TextBody
    ): Message?

    @POST("/v2/workspaces/{workspaceId}/channels/{channelId}/attachments")
    suspend fun createMessage(
        @Path("workspaceId") workspaceId: String,
        @Path("channelId") channelId: String,
        @Body path: RequestBody
    ): Message?

    @GET("/v2/workspaces/{workspaceId}/channels/{channelId}/messages/{messageId}")
    suspend fun messageDetail(
        @Path("workspaceId") workspaceId: String,
        @Path("channelId") channelId: String,
        @Path("messageId") messageId: String,
    ): Message?

    @POST("/v2/workspaces/{workspaceId}/channels/{channelId}/messages/{messageId}")
    suspend fun putMessage(
        @Path("workspaceId") workspaceId: String,
        @Path("channelId") channelId: String,
        @Path("messageId") messageId: String
    ): Message?

    @POST("/v2/workspaces/{workspaceId}/channels/{channelId}/messages/{messageId}/hide")
    suspend fun hideMessage(
        @Path("workspaceId") workspaceId: String,
        @Path("channelId") channelId: String,
        @Path("messageId") messageId: String
    ): Message?

    @POST("/v2/workspaces/{workspaceId}/channels/{channelId}/messages/{messageId}/revoke")
    suspend fun revokeMessage(
        @Path("workspaceId") workspaceId: String,
        @Path("channelId") channelId: String,
        @Path("messageId") messageId: String
    ): Message?


    @POST("/v2/workspaces/{workspaceId}/channels/{channelId}/messages")
    suspend fun getReactionMessage(
        @Path("workspaceId") workspaceId: String,
        @Path("channelId") channelId: String,
        @Body body: MessageSendBody
    ): Message?

    @DELETE("/v2/workspaces/{workspaceId}/channels/{channelId}/messages/{messageId}/reactions/@me")
    suspend fun deleteMeReactionMessage(
        @Path("workspaceId") workspaceId: String,
        @Path("channelId") channelId: String,
        @Body body: MessageSendBody
    ): Message?

    @GET("v2/crawler/crawler?")
    suspend fun crawler(
        @Path("url") url: String
    ): Any?
}
