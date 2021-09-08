package com.halo.data.api.role

import com.halo.data.entities.response.ResponsePaging
import com.halo.data.entities.role.RoleChannel

interface RoleRestApi {

    suspend fun getRoleChannel(
        token: String,
        workspaceId: String,
        channelId: String,
        since: String?,
        limit: Int
    ): ResponsePaging<RoleChannel>?
}