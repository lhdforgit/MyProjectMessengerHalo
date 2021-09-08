package com.halo.data.api.role

import com.halo.data.api.utils.ServiceGenerator
import com.halo.data.entities.response.ResponsePaging
import com.halo.data.entities.role.RoleChannel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoleRestApiImpl @Inject constructor() : RoleRestApi {

    override suspend fun getRoleChannel(
        token: String,
        workspaceId: String,
        channelId: String,
        since: String?,
        limit: Int
    ): ResponsePaging<RoleChannel>? = ServiceGenerator.createMessService(
        serviceClass = RoleService::class.java,
        authorization = token
    ).getRole(workspaceId, channelId, since, limit)
}