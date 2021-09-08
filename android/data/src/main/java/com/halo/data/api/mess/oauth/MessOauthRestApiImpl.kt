package com.halo.data.api.mess.oauth

import com.halo.data.api.utils.ServiceGenerator
import com.halo.data.entities.mess.VerifyBody
import com.halo.data.entities.mess.halo.HaloAuthorBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessOauthRestApiImpl
@Inject constructor() : MessOauthRestApi {
    private val service = ServiceGenerator.createMessService(MessOauthService::class.java)

    override suspend fun authorize(
        body: VerifyBody
    ) = service.authorize(body)

    override suspend fun verify(
        body: VerifyBody
    ) = service.verify(body)

    override suspend fun haloAuthorize(
        body: HaloAuthorBody?,
        token: String?
    ) = if (token?.isNotEmpty() == true) {
        /** Đăng nhập Hahalolo bằng Token */
        ServiceGenerator.createMessService(
            MessOauthService::class.java,
            token
        ).haloAuthorizeToken()
    } else {
        /** Đăng nhập Hahalolo bằng username và password */
        body?.run {
            service.haloAuthorize(this)
        }
    }
}