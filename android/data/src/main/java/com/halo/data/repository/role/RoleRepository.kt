package com.halo.data.repository.role

import com.halo.data.entities.role.RoleChannel
import kotlinx.coroutines.flow.Flow

interface RoleRepository {

    suspend fun getRoleChannel(
        token: String,
        workspaceId: String = "0",
        channelId: String
    ): Flow<MutableList<RoleChannel>>
}