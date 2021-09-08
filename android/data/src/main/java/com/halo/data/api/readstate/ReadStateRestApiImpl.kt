package com.halo.data.api.readstate

import com.halo.data.api.utils.ServiceGenerator
import com.halo.data.entities.readstate.ReadState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReadStateRestApiImpl @Inject constructor() : ReadStateRestApi {

    override suspend fun lastSeen(
        token: String?,
        workspaceId: String?
    ): MutableList<ReadState>? = ServiceGenerator.createMessService(
        serviceClass = ReadStateService::class.java,
        authorization = token ?: ""
    ).lastSeen(workspaceId)

    override suspend fun lastSeen(
        token: String?,
        workspaceId: String?,
        channelId: String?
    ): ReadState? = ServiceGenerator.createMessService(
        serviceClass = ReadStateService::class.java,
        authorization = token ?: ""
    ).lastSeen(workspaceId, channelId)
}