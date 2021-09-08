/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.api.member

import com.halo.data.entities.member.*
import com.halo.data.entities.response.ResponsePaging
import retrofit2.http.*

/**
 * @author ngannd
 * Create by ngannd on 24/05/2019
 */
interface MemberService {

    @GET("/v2/workspaces/{workspaceId}/channels/{channelId}/members")
    suspend fun getMemberPaging(
        @Path("workspaceId") workspaceId: String?,
        @Path("channelId") channelId: String?,
        @Query("since") since: String?,
        @Query("limit") limit: Int
    ): ResponsePaging<MemberChannel>?

    // Creator A.Son
    @GET("/v2/workspaces/{workspaceId}/channels/{channelId}/members")
    suspend fun getMemberPaging2(
        @Path("workspaceId") workspaceId: String?,
        @Path("channelId") channelId: String?,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): ResponsePaging<Member>?

    @GET("/v2/workspaces/{workspaceId}/channels/{channelId}/members/{userId}")
    suspend fun memberDetail(
        @Path("workspaceId") workspaceId: String?,
        @Path("channelId") channelId: String?,
        @Path("userId") userId: String?
    ): MemberChannel?

    @PUT("/v2/workspaces/{workspaceId}/channels/{channelId}/members/{userId}")
    suspend fun updateMember(
        @Path("workspaceId") workspaceId: String?,
        @Path("channelId") channelId: String?,
        @Path("userId") userId: String?,
        @Body body: UpdateMemberBody
    ): MemberChannel?

    @DELETE("/v2/workspaces/{workspaceId}/channels/{channelId}/members/{userId}")
    suspend fun deleteMember(
        @Path("workspaceId") workspaceId: String?,
        @Path("channelId") channelId: String?,
        @Path("userId") userId: String?
    ): MemberChannel?

    @POST("/v2/workspaces/{workspaceId}/channels/{channelId}/members/leave")
    suspend fun leaveChannel(
        @Path("workspaceId") workspaceId: String?,
        @Path("channelId") channelId: String?,
        @Body body: LeaveChannelBody
    ): MemberChannel?

    @POST("/v2/workspaces/{workspaceId}/channels/{channelId}/members/{userId}/roles")
    suspend fun setRoleMember(
        @Path("workspaceId") workspaceId: String,
        @Path("channelId") channelId: String,
        @Path("userId") userId: String,
        @Body body: MemberRoleBody
    ): MemberChannel?

    @HTTP(method = "DELETE", path = "/v2/workspaces/{workspaceId}/channels/{channelId}/members/{userId}/roles", hasBody = true)
    suspend fun deleteRoleMember(
        @Path("workspaceId") workspaceId: String,
        @Path("channelId") channelId: String,
        @Path("userId") userId: String,
        @Body body: MemberRoleBody
    ): MemberChannel?
}
