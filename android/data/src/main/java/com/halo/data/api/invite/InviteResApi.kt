package com.halo.data.api.invite

import com.halo.data.entities.invite.*
import retrofit2.http.POST
import retrofit2.http.Path

interface InviteResApi {

    suspend fun createInvite(token: String, body: CreateInviteBody): CreateInviteResponse?

    suspend fun sendInvite(token: String, body: SendInviteBody): SendInviteResponse?


    suspend fun inviteInfo(token: String, code: String): InviteInfoResponse?

    suspend fun inviteJoin(token: String, code: String): InviteJoinResponse?
}