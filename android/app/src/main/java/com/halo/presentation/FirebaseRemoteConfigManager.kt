/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation

import com.halo.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Create by ndn
 * Create on 7/14/20
 * com.halo.presentation
 */
@Singleton
class FirebaseRemoteConfigManager
@Inject constructor() {

    var appVersionCode = BuildConfig.VERSION_CODE

    var firebaseRemoteConfigLastVersionCode: Double = -1.0
    var firebaseRemoteConfigForceVersionCode: Double = -1.0

    val isForceVersionApp: Boolean
        get() = appVersionCode < firebaseRemoteConfigForceVersionCode

    val isLastVersionApp: Boolean
        get() = appVersionCode < firebaseRemoteConfigLastVersionCode
}