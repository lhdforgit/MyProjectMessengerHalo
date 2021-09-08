package com.halo.data.api.presence

import com.halo.data.api.utils.ServiceGenerator
import com.halo.data.entities.attachment.Attachment
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PresenceRestApiImpl @Inject constructor() : PresenceRestApi {

    override suspend fun status(token: String)= ServiceGenerator.createMessService(
        serviceClass = PresenceService::class.java,
        authorization = token
    ).status()
    override suspend fun me(token: String)= ServiceGenerator.createMessService(
        serviceClass = PresenceService::class.java,
        authorization = token
    ).me()
}