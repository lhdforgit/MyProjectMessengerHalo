package com.halo.data.api.attachment

import com.halo.data.api.utils.ServiceGenerator
import com.halo.data.entities.attachment.Attachment
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttachmentRestApiImpl @Inject constructor() : AttachmentRestApi {

    override suspend fun postAttachments(
        token: String,
        workspaceId: String,
        channelId: String?,
        attachments: List<MultipartBody.Part>,
        ref : MultipartBody.Part
    ) = ServiceGenerator.createMessService(
        serviceClass = AttachmentService::class.java,
        authorization = token
    ).postAttachments(workspaceId, channelId, attachments, ref)

    override suspend fun getAttachments(
        token: String,
        workspaceId: String,
        channelId: String?,
        limit: Int,
        since: String?,
        type: String
    ) = ServiceGenerator.createMessService(
        serviceClass = AttachmentService::class.java,
        authorization = token
    ).getAttachments(workspaceId, channelId, limit, since, type)
}