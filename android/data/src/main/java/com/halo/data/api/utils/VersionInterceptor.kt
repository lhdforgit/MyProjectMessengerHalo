/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.api.utils

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * @author ngannd
 * Create by ngannd on 06/12/2018
 */
class VersionInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestWithUserAgent = originalRequest.newBuilder()
                .header("version", "4")
                .build()
        return chain.proceed(requestWithUserAgent)
    }
}