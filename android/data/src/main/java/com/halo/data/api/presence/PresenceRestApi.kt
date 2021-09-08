package com.halo.data.api.presence

import com.halo.data.entities.attachment.Attachment
import com.halo.data.entities.attachment.AttachmentResponse
import okhttp3.MultipartBody
import retrofit2.http.GET

interface PresenceRestApi {


    suspend fun status(token:String): Any?

    suspend fun me(token:String): Any?
}