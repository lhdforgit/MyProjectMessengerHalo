package com.halo.data.api.mess.oauth

import com.halo.data.entities.mess.StatusResponse
import com.halo.data.entities.mess.VerifyBody
import com.halo.data.entities.mess.VerifyResponse
import com.halo.data.entities.mess.halo.HaloAuthorBody
import com.halo.data.entities.mess.halo.HaloAuthorResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MessOauthService {

    @POST("/v2/iam/verify")
    suspend fun verify(@Body body: VerifyBody): VerifyResponse?

    @POST("/v2/iam/authorize")
    suspend fun authorize(@Body body: VerifyBody): VerifyResponse?

    @POST("/v2/iam/providers/halo/authorize")
    suspend fun haloAuthorize(@Body body: HaloAuthorBody): HaloAuthorResponse?

    @POST("/v2/iam/providers/halo/authorize")
    suspend fun haloAuthorizeToken(): HaloAuthorResponse?
}