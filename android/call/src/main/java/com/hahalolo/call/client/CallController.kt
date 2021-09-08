package com.hahalolo.call.client

import android.app.Application
import android.content.SharedPreferences
import com.hahalolo.call.webrtc.QBRTCSession
import com.halo.data.entities.mess.oauth.MessOauth
import com.halo.data.entities.mess.oauth.OauthInfo
import com.halo.data.entities.user.User

/**
 * Create by ndn
 * Create on 6/17/21
 * com.hahalolo.call.client.utils
 */
interface CallController {

    val app: Application

    val oauth: MessOauth?
    val oauthInfo: OauthInfo?
    val oauthInfoName: String?

    val sharedPreferences: SharedPreferences

    val callWithUser: MutableList<User>?
    val callWithUserIds: MutableList<Int>?

    val currentSession: QBRTCSession?
    fun setCurrentSession(session: QBRTCSession?)
}