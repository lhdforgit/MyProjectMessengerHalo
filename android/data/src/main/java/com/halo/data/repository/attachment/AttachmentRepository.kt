package com.halo.data.repository.attachment

import androidx.paging.PagingData
import com.halo.data.common.resource.Resource
import com.halo.data.entities.attachment.Attachment
import com.halo.data.entities.attachment.AttachmentBody
import com.halo.data.entities.attachment.AttachmentResponse
import kotlinx.coroutines.flow.Flow

interface AttachmentRepository {

    suspend fun postAttachments(
        token: String,
        body: AttachmentBody
    ): Resource<AttachmentResponse>?

    suspend fun getAttachmentsPaging(
        token: String,
        workspaceId: String = "0",
        channelId: String,
        type: String
    ): Flow<PagingData<Attachment>>

    suspend fun getAttachments(
        token: String,
        workspaceId: String = "0",
        channelId: String,
        type: String
    ): Flow<Resource<MutableList<Attachment>>>?
}