package com.halo.data.api.invite

import com.halo.data.entities.invite.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface InviteService {

    @POST("/v2/invitations")
    suspend fun createInvite(@Body body: CreateInviteBody): CreateInviteResponse?

    @POST("/v2/invitations/invite")
    suspend fun sendInvite(@Body body: SendInviteBody): SendInviteResponse?


    @GET("/v2/invitations/{code}")
    suspend fun inviteInfo(@Path("code") code: String): InviteInfoResponse?

    @GET("/v2/invitations/{code}/join")
    suspend fun inviteJoin(@Path("code") code: String): InviteJoinResponse?
}