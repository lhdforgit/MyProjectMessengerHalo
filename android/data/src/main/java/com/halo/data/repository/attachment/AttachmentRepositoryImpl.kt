package com.halo.data.repository.attachment

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.halo.data.api.attachment.AttachmentRestApi
import com.halo.data.common.resource.Resource
import com.halo.data.entities.attachment.Attachment
import com.halo.data.entities.attachment.AttachmentBody
import com.halo.data.entities.attachment.AttachmentResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttachmentRepositoryImpl @Inject
constructor(private val attachmentRestApi: AttachmentRestApi) :
    AttachmentRepository {

    override suspend fun postAttachments(token: String, body: AttachmentBody) = kotlin.runCatching {
        attachmentRestApi.postAttachments(
            token = token,
            workspaceId = body.workspaceId,
            channelId = body.channelId,
            attachments = body.listAttachment(),
            ref = body.refPart()
        )?.run {
            Resource.success(this)
        } ?: kotlin.run {
            Resource.error<AttachmentResponse>(500, "errors", null)
        }
    }.getOrElse {
        Resource.error(it)
    }

    override suspend fun getAttachmentsPaging(
        token: String,
        workspaceId: String,
        channelId: String,
        type: String
    ): Flow<PagingData<Attachment>> =
        Pager(PagingConfig(pageSize = 20)) {
            AttachmentPagingSource(
                api = attachmentRestApi,
                token = token,
                workspaceId = workspaceId,
                channelId = channelId,
                type = type
            )
        }.flow

    override suspend fun getAttachments(
        token: String,
        workspaceId: String,
        channelId: String,
        type: String
    ) = flow {
        emit(Resource.loading<MutableList<Attachment>>(null))
        val resource = kotlin.runCatching {
            attachmentRestApi.getAttachments(
                token = token,
                workspaceId = workspaceId,
                channelId = channelId,
                limit = 20,
                since = null,
                type = type
            )?.docs?.run {
                Resource.success(this)
            } ?: kotlin.run {
                Resource.error<MutableList<Attachment>>(500, "errors", null)
            }
        }.getOrElse {
            Resource.error(it)
        }
        emit(resource)
    }.catch {
        Resource.error<MutableList<Attachment>>(it)
    }.flowOn(Dispatchers.IO)
}