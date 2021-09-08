/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation

import com.google.gson.Gson
import com.halo.data.entities.mess.oauth.MessOauth
import com.halo.data.entities.mess.oauth.OauthInfo

/**
 * @author ndn
 * Created by ndn
 * Created on 2019-12-12
 * com.halo.presentation
 */

val messApplication: MessApplication
    get() = MessApplication.instance
val appLang: String
    get() = "vi"

val oauth: MessOauth?
    get() = messApplication.oauth
val oauthInfo: OauthInfo?
    get() = oauth?.run {
        Gson().fromJson(this.info, OauthInfo::class.java)
    }

val isOpenBubbleSetting : Boolean
    get() = messApplication.isOpenBubble

val isForceVersionApp: Boolean
    get() = messApplication.firebaseConfigManager.isForceVersionApp
val isLastVersionApp: Boolean
    get() = messApplication.firebaseConfigManager.isLastVersionApp

