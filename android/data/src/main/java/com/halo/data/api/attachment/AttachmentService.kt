package com.halo.data.api.attachment

import com.halo.data.entities.attachment.Attachment
import com.halo.data.entities.attachment.AttachmentResponse
import com.halo.data.entities.response.ResponsePaging
import okhttp3.MultipartBody
import retrofit2.http.*

interface AttachmentService {

    @Multipart
    @POST("/v2/workspaces/{workspaceId}/channels/{channelId}/attachments")
    suspend fun postAttachments(
        @Path("workspaceId") workspaceId: String?,
        @Path("channelId") channelId: String?,
        @Part attachments: List<MultipartBody.Part>,
        @Part ref : MultipartBody.Part
    ): AttachmentResponse?

    @GET("/v2/workspaces/{workspaceId}/channels/{channelId}/attachments")
    suspend fun getAttachments(
        @Path("workspaceId") workspaceId: String?,
        @Path("channelId") channelId: String?,
        @Query("limit") limit: Int,
        @Query("since") since: String?,
        @Query("type") type: String
    ): ResponsePaging<Attachment>?
}