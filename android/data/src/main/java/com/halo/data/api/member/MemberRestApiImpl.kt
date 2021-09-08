/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.api.member

import com.halo.data.api.utils.ServiceGenerator
import com.halo.data.entities.member.*
import com.halo.data.entities.response.ResponsePaging
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author ndn
 * Created by ndn
 * Created on 9/6/18
 */
@Singleton
class MemberRestApiImpl @Inject
internal constructor() : MemberRestApi {

    override suspend fun getMemberPaging(
        token: String,
        workspaceId: String?,
        channelId: String?,
        since: String?,
        limit: Int
    ): ResponsePaging<MemberChannel>? = ServiceGenerator.createMessService(
        serviceClass = MemberService::class.java,
        authorization = token
    ).getMemberPaging(workspaceId, channelId, since, limit)

    override suspend fun getMemberPaging2(
        token: String,
        workspaceId: String?,
        channelId: String?,
        page: Int,
        limit: Int
    ): ResponsePaging<Member>? = ServiceGenerator.createMessService(
        serviceClass = MemberService::class.java,
        authorization = token
    ).getMemberPaging2(workspaceId, channelId, page, limit)

    override suspend fun memberDetail(
        token: String,
        workspaceId: String?,
        channelId: String?,
        userId: String?
    ) = ServiceGenerator.createMessService(
        serviceClass = MemberService::class.java,
        authorization = token
    ).memberDetail(workspaceId, channelId, userId)

    override suspend fun updateMember(
        token: String,
        workspaceId: String?,
        channelId: String?,
        userId: String?,
        body: UpdateMemberBody
    ) = ServiceGenerator.createMessService(
        serviceClass = MemberService::class.java,
        authorization = token
    ).updateMember(workspaceId, channelId, userId, body)

    override suspend fun deleteMember(
        token: String,
        workspaceId: String?,
        channelId: String?,
        userId: String?
    ) = ServiceGenerator.createMessService(
        serviceClass = MemberService::class.java,
        authorization = token
    ).deleteMember(workspaceId, channelId, userId)

    override suspend fun leaveChannel(
        token: String,
        workspaceId: String?,
        channelId: String?,
        body: LeaveChannelBody
    ): MemberChannel? = ServiceGenerator.createMessService(
        serviceClass = MemberService::class.java,
        authorization = token
    ).leaveChannel(workspaceId, channelId, body)

    override suspend fun setRoleMember(
        token: String,
        workspaceId: String,
        channelId: String,
        userId: String,
        body: MemberRoleBody
    ): MemberChannel? = ServiceGenerator.createMessService(
        serviceClass = MemberService::class.java,
        authorization = token
    ).setRoleMember(workspaceId, channelId, userId, body)

    override suspend fun deleteRoleMember(
        token: String,
        workspaceId: String,
        channelId: String,
        userId: String,
        body: MemberRoleBody
    ): MemberChannel? = ServiceGenerator.createMessService(
        serviceClass = MemberService::class.java,
        authorization = token
    ).deleteRoleMember(workspaceId, channelId, userId, body)


}