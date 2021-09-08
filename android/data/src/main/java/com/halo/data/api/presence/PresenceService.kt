package com.halo.data.api.presence

import com.halo.data.entities.attachment.Attachment
import com.halo.data.entities.attachment.AttachmentResponse
import okhttp3.MultipartBody
import retrofit2.http.*

interface PresenceService {

    @GET("/v2/presence/status")
    suspend fun status(): Any?

    @GET("/v2/presence/me")
    suspend fun me(): Any?
}