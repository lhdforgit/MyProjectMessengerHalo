package com.halo.data.api.readstate

import com.halo.data.entities.attachment.Attachment
import com.halo.data.entities.attachment.AttachmentResponse
import com.halo.data.entities.readstate.ReadState
import com.halo.data.entities.response.ResponsePaging
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ReadStateRestApi {

    suspend fun lastSeen(
        token: String?,
        workspaceId: String?,
    ): MutableList<ReadState>?

    suspend fun lastSeen(
        token: String?,
        workspaceId: String?,
        channelId: String?,
    ): ReadState?
}