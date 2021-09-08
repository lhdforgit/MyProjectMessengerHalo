package com.halo.data.api.role

import com.halo.data.entities.response.ResponsePaging
import com.halo.data.entities.role.RoleChannel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RoleService {
    @GET("/v2/workspaces/{workspaceId}/channels/{channelId}/roles")
    suspend fun getRole(
        @Path("workspaceId") workspaceId: String,
        @Path("channelId") channelId: String,
        @Query("since") since: String?,
        @Query("limit") limit: Int) : ResponsePaging<RoleChannel>?
}