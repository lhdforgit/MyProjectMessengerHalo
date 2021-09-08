package com.hahalolo.incognito.presentation.controller

import com.halo.data.entities.mess.oauth.MessOauth
import com.halo.data.entities.mess.oauth.OauthInfo

interface IncognitoController {
    val oauth: MessOauth?
    val oauthInfo: OauthInfo?

    fun setOauthToApplication(
        oauth: MessOauth?
    )

    fun startLogin()
}