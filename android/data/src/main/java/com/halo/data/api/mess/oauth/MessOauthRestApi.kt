package com.halo.data.api.mess.oauth

import com.halo.data.entities.mess.VerifyBody
import com.halo.data.entities.mess.VerifyResponse
import com.halo.data.entities.mess.halo.HaloAuthorBody
import com.halo.data.entities.mess.halo.HaloAuthorResponse

interface MessOauthRestApi {

    suspend fun verify(
        body: VerifyBody
    ): VerifyResponse?

    suspend fun authorize(
        body: VerifyBody
    ): VerifyResponse?

    suspend fun haloAuthorize(
        body: HaloAuthorBody?,
        token: String?
    ): HaloAuthorResponse?
}