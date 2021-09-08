/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.utils

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.TelephonyManager
import androidx.core.os.ConfigurationCompat
import com.halo.constant.HaloConfig

/**
 * @author ndn
 * Created by ndn
 * Created on 2019-11-13
 * com.halo.common.utils
 */

object CountryCodeUtil {

    /**
     * Get ISO 3166-1 alpha-2 country code for this device (or null if not available)
     * @param context Context reference to get the TelephonyManager instance from
     * @return country code or [HaloConfig.ISO_DEFAULT]
     */
    @JvmStatic
    fun getDefaultCountryCode(context: Context?): String {
        return try {
            context?.run {
                getDefaultCountryCodeBySim(context, context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager)
            } ?: HaloConfig.ISO_DEFAULT
        } catch (e: Exception) {
            e.printStackTrace()
            HaloConfig.ISO_DEFAULT
        }
    }

    @SuppressLint("DefaultLocale")
    @JvmStatic
    fun getDefaultCountryCodeBySim(context: Context?, tm: TelephonyManager): String {
        return try {
            context?.run {
                tm.simCountryIso?.takeIf { it.length == 2 }?.run { toLowerCase() }
                        ?: getDefaultCountryCodeByNetwork(context, tm)
            } ?: getDefaultCountryCodeByConfiguration(context)
        } catch (e: Exception) {
            e.printStackTrace()
            getDefaultCountryCodeByConfiguration(context)
        }
    }

    @SuppressLint("DefaultLocale")
    @JvmStatic
    fun getDefaultCountryCodeByNetwork(context: Context?, tm: TelephonyManager): String {
        return try {
            context?.run {
                if (tm.phoneType != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                    tm.networkCountryIso?.takeIf { it.length == 2 }?.run { toLowerCase() }
                            ?: getDefaultCountryCodeByConfiguration(context)
                } else getDefaultCountryCodeByConfiguration(context)
            } ?: getDefaultCountryCodeByConfiguration(context)
        } catch (e: Exception) {
            e.printStackTrace()
            getDefaultCountryCodeByConfiguration(context)
        }
    }

    @JvmStatic
    fun getDefaultCountryCodeByConfiguration(context: Context?): String {
        return try {
            context?.run {
                ConfigurationCompat.getLocales(context.resources.configuration)[0].country
                        ?: HaloConfig.ISO_DEFAULT
            } ?: HaloConfig.ISO_DEFAULT
        } catch (e: Exception) {
            e.printStackTrace()
            HaloConfig.ISO_DEFAULT
        }
    }
}