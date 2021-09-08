package com.halo.data.api.invite

import com.halo.data.api.utils.ServiceGenerator
import com.halo.data.entities.invite.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InviteResApiImpl @Inject constructor() : InviteResApi {

    override suspend fun createInvite(
        token: String,
        body: CreateInviteBody
    ): CreateInviteResponse? = ServiceGenerator.createMessService(
        serviceClass = InviteService::class.java,
        authorization = token
    ).createInvite(body)

    override suspend fun sendInvite(token: String, body: SendInviteBody): SendInviteResponse? =
        ServiceGenerator.createMessService(
            serviceClass = InviteService::class.java,
            authorization = token
        ).sendInvite(body)

    override suspend fun inviteInfo(token: String, code: String): InviteInfoResponse? =
        ServiceGenerator.createMessService(
            serviceClass = InviteService::class.java,
            authorization = token
        ).inviteInfo(code)

    override suspend fun inviteJoin(token: String, code: String): InviteJoinResponse? =
        ServiceGenerator.createMessService(
            serviceClass = InviteService::class.java,
            authorization = token
        ).inviteJoin(code)
}