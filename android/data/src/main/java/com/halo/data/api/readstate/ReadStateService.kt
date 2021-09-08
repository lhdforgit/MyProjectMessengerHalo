package com.halo.data.api.readstate

import com.halo.data.entities.attachment.Attachment
import com.halo.data.entities.attachment.AttachmentResponse
import com.halo.data.entities.readstate.ReadState
import com.halo.data.entities.response.ResponsePaging
import okhttp3.MultipartBody
import retrofit2.http.*

interface ReadStateService {

    @GET("/v2/readstate/workspaces/{workspaceId}/channels/last-seen")
    suspend fun lastSeen(
        @Path("workspaceId") workspaceId: String?,
    ): MutableList<ReadState>?

    @GET("/v2/readstate/workspaces/{workspaceId}/channels/last-seen/{channelId}")
    suspend fun lastSeen(
        @Path("workspaceId") workspaceId: String?,
        @Path("channelId") channelId: String?,
    ): ReadState?

    @POST("/v2/readstate/workspaces/{workspaceId}/channels/{channelId}")
    suspend fun setLastSeen(
        @Path("workspaceId") workspaceId: String?,
        @Path("channelId") channelId: String?,
    ): ReadState?

    @GET("/v2/readstate/workspaces/{workspaceId}/channels/{channelId}/messages/{messageId}")
    suspend fun readState(
        @Path("workspaceId") workspaceId: String?,
        @Path("channelId") channelId: String?,
        @Path("messageId") messageId: String?=null ,
    ): Any?

    @GET("/v2/readstate/workspaces/{workspaceId}/channels/{channelId}/messages")
    suspend fun readState(
        @Path("workspaceId") workspaceId: String?,
        @Path("channelId") channelId: String?,
        @Query("ids") ids: MutableList<String>,
    ): Any?
}