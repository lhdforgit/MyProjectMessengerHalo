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
 * @author ndn
 * Created by ndn
 * Created on 8/21/18
 */
interface MemberRestApi {

    suspend fun getMemberPaging(
        token: String,
        workspaceId: String?,
        channelId: String?,
        since : String?,
        limit: Int
    ): ResponsePaging<MemberChannel>?

    suspend fun getMemberPaging2(
        token: String,
        workspaceId: String?,
        channelId: String?,
        page : Int,
        limit: Int
    ): ResponsePaging<Member>?

    suspend fun memberDetail(
        token: String,
        workspaceId : String?,
        channelId: String?,
        userId: String?
    ) : MemberChannel?

    suspend fun updateMember(
        token: String,
        workspaceId : String?,
        channelId: String?,
        userId: String?,
        body: UpdateMemberBody
    ) : MemberChannel?

    suspend fun deleteMember(
        token: String,
        workspaceId : String?,
        channelId: String?,
        userId: String?
    ) : MemberChannel?

    suspend fun leaveChannel(
        token: String,
        workspaceId : String?,
        channelId: String?,
        body : LeaveChannelBody
    ) : MemberChannel?

    suspend fun setRoleMember(
        token: String,
        workspaceId : String,
        channelId: String,
        userId: String,
        body : MemberRoleBody
    ) : MemberChannel?

    suspend fun deleteRoleMember(
        token: String,
        workspaceId : String,
        channelId: String,
        userId: String,
        body : MemberRoleBody
    ) : MemberChannel?
}
