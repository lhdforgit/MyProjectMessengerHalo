/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.base

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * @author ndn
 * Created by ndn
 * Created on 3/27/20
 * com.halo.presentation.base
 */

class FirebaseAnalyticsManager(context: Context) {

    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    init {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context)
    }

    /*  Google Analytics  */

    fun logEventStartApp() {
        logEventsGoogleAnalytic(
            id = System.currentTimeMillis().toString(),
            name = "",
            event = START_APP
        )
    }

    fun logEventSignUp(name: String?) {
        logEventsGoogleAnalytic(
            id = System.currentTimeMillis().toString(),
            name = name,
            event = FirebaseAnalytics.Event.SIGN_UP
        )
    }

    fun logEventVerifyOTP(id: String?, name: String?) {
        logEventsGoogleAnalytic(
            id = id,
            name = name,
            event = VERIFY_OTP
        )
    }

    fun logEventLogin(name: String?) {
        logEventsGoogleAnalytic(
            id = System.currentTimeMillis().toString(),
            name = name,
            event = FirebaseAnalytics.Event.LOGIN
        )
    }

    fun logEventForgotPassword(name: String?) {
        logEventsGoogleAnalytic(
            id = System.currentTimeMillis().toString(),
            name = name,
            event = FORGOT_PASSWORD
        )
    }

    fun logEventShareByHahalolo() {
        logEventsGoogleAnalytic(
            id = System.currentTimeMillis().toString(),
            name = "",
            event = SHARE_BY_APP
        )
    }

    fun logEventDeepLink(name: String?) {
        logEventsGoogleAnalytic(
            id = System.currentTimeMillis().toString(),
            name = name,
            event = DEEP_LINK_APP
        )
    }

    fun logEventReaction(id: String?, name: String?) {
        logEventsGoogleAnalytic(
            id = id,
            name = name,
            event = REACTION
        )
    }

    fun logEventComment(name: String?) {
        logEventsGoogleAnalytic(
            id = System.currentTimeMillis().toString(),
            name = name,
            event = COMMENT
        )
    }

    fun logEventShare(name: String?) {
        logEventsGoogleAnalytic(
            id = System.currentTimeMillis().toString(),
            name = name,
            event = SHARE
        )
    }

    fun logEventCreatePost(name: String?) {
        logEventsGoogleAnalytic(
            id = System.currentTimeMillis().toString(),
            name = name,
            event = CREATE_POST
        )
    }

    private fun logEventsGoogleAnalytic(id: String?, name: String?, event: String) {
        try {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id)
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name)
            mFirebaseAnalytics?.logEvent(event, bundle)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        const val START_APP = "start_app"
        const val VERIFY_OTP = "verify_otp"
        const val FORGOT_PASSWORD = "forgot_password"
        const val SHARE_BY_APP = "share_by_app"
        const val DEEP_LINK_APP = "deep_link_app"
        const val REACTION = "reaction"
        const val COMMENT = "comment"
        const val SHARE = "share"
        const val CREATE_POST = "create_post"
    }
}