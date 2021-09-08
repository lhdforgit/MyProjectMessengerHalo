package com.halo.presentation.controller.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.hahalolo.call.client.CallController
import com.hahalolo.call.client.utils.SHARED_PREFS_NAME
import com.hahalolo.call.webrtc.QBRTCSession
import com.halo.data.entities.mess.oauth.MessOauth
import com.halo.data.entities.mess.oauth.OauthInfo
import com.halo.data.entities.user.User
import com.halo.presentation.messApplication
import javax.inject.Inject

/**
 * Create by ndn
 * Create on 6/18/21
 * com.halo.presentation.controller.module
 */
class CallControllerImpl
@Inject constructor() : CallController {

    override val app: Application
        get() = messApplication

    override val oauth: MessOauth?
        get() = messApplication.oauth
    override val oauthInfo: OauthInfo?
        get() = messApplication.oauthInfo
    override val oauthInfoName: String
        get() = messApplication.oauthInfo?.firstName + messApplication.oauthInfo?.lastName

    override val sharedPreferences: SharedPreferences
        get() = messApplication.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    override val callWithUser: MutableList<User>?
        get() = messApplication.callWithUser
    override val callWithUserIds: MutableList<Int>?
        get() = callWithUser?.run {
            val ids: MutableList<Int> = ArrayList()
            this.forEach {
                ids.add(123)
            }
            ids
        }

    override val currentSession: QBRTCSession?
        get() = messApplication.currentSession

    override fun setCurrentSession(session: QBRTCSession?) {
        messApplication.currentSession = session
    }
}