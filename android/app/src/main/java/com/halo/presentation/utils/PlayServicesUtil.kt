/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.presentation.utils

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

object PlayServicesUtil {
    private val TAG = PlayServicesUtil::class.java.simpleName

    @JvmStatic
    fun getPlayServicesStatus(context: Context): PlayServicesStatus {
        var gcmStatus = 0
        gcmStatus = try {
            GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(context)
        } catch (t: Throwable) {
            Log.w(TAG, t)
            return PlayServicesStatus.MISSING
        }
        Log.i(
            TAG,
            "Play Services: $gcmStatus"
        )
        return when (gcmStatus) {
            ConnectionResult.SUCCESS -> PlayServicesStatus.SUCCESS
            ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED -> {
                try {
                    val applicationInfo =
                        context.packageManager.getApplicationInfo("com.google.android.gms", 0)
                    if (applicationInfo != null && !applicationInfo.enabled) {
                        return PlayServicesStatus.MISSING
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    Log.w(TAG, e)
                }
                PlayServicesStatus.NEEDS_UPDATE
            }
            ConnectionResult.SERVICE_DISABLED,
            ConnectionResult.SERVICE_MISSING,
            ConnectionResult.SERVICE_INVALID,
            ConnectionResult.API_UNAVAILABLE,
            ConnectionResult.SERVICE_MISSING_PERMISSION -> PlayServicesStatus.MISSING
            else -> PlayServicesStatus.TRANSIENT_ERROR
        }
    }

    enum class PlayServicesStatus {
        SUCCESS, MISSING, NEEDS_UPDATE, TRANSIENT_ERROR
    }
}