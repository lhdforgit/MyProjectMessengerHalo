package com.halo.data.repository.invite

import com.halo.data.common.resource.Resource
import com.halo.data.entities.invite.*
import kotlinx.coroutines.flow.Flow

interface InviteRepository {

    suspend fun createUrlInvite(
        token: String,
        body: CreateInviteBody
    ): CreateInviteResponse?

    suspend fun sendInviteUser(
        token: String,
        body: SendInviteBody
    ): SendInviteResponse?

    suspend fun sendInviteFlow(
        token: String,
        body: SendInviteBody
    ): Flow<Resource<SendInviteResponse>>?


    suspend fun inviteInfo(token: String, code: String): Flow<Resource<InviteInfoResponse>>

    suspend fun inviteJoin(token: String, code: String): Flow<Resource<InviteJoinResponse>>

}