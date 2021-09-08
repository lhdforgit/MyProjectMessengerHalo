package com.halo.presentation.controller.module

import android.content.Intent
import com.hahalolo.incognito.presentation.controller.IncognitoController
import com.halo.data.entities.mess.oauth.MessOauth
import com.halo.data.entities.mess.oauth.OauthInfo
import com.halo.presentation.MessApplication
import com.halo.presentation.messApplication
import com.halo.presentation.startapp.start.StartAct
import javax.inject.Inject

/**
 * Create by ndn
 * Create on 6/9/21
 * com.halo.presentation.controller.module
 */
class IncognitoControllerImpl
@Inject constructor() : IncognitoController {

    override val oauth: MessOauth?
        get() = MessApplication.instance.oauth
    override val oauthInfo: OauthInfo?
        get() = MessApplication.instance.oauthInfo

    override fun setOauthToApplication(oauth: MessOauth?) {
        messApplication.oauth = oauth
    }

    override fun startLogin() {
        val intent = StartAct.getIntent(messApplication)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        messApplication.startActivity(intent)
    }
}