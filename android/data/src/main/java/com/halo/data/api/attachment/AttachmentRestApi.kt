package com.halo.data.api.attachment

import com.halo.data.entities.attachment.Attachment
import com.halo.data.entities.attachment.AttachmentResponse
import com.halo.data.entities.response.ResponsePaging
import okhttp3.MultipartBody

interface AttachmentRestApi {

    suspend fun postAttachments(
        token: String,
        workspaceId: String,
        channelId: String?,
        attachments: List<MultipartBody.Part>,
        ref : MultipartBody.Part
    ): AttachmentResponse?

    suspend fun getAttachments(
        token: String,
        workspaceId: String,
        channelId: String?,
        limit: Int,
        since: String?,
        type: String
    ): ResponsePaging<Attachment>?
}