/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.api.utils

import com.google.common.net.HttpHeaders
import com.halo.common.utils.DeviceUtils
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * @author ngannd
 * Create by ngannd on 06/12/2018
 */
class UserAgentInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestWithUserAgent = originalRequest.newBuilder()
                .header(HttpHeaders.USER_AGENT, DeviceUtils.getModel() + "/"
                        + DeviceUtils.getManufacturer() + DeviceUtils.getAndroidID())
                .build()
        return chain.proceed(requestWithUserAgent)
    }
}