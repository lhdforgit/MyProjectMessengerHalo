/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.api.utils

import com.google.common.net.HttpHeaders
import com.halo.common.utils.DeviceUtils
import com.halo.constant.HaloConfig.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 * @author ndn
 * Create by ndn
 * Create on 15.05.2018
 */

object ServiceGenerator {

    private val builder = Retrofit.Builder()
        .baseUrl(MESS_HOST)
        .addCallAdapterFactory(LiveDataCallAdapterFactory())
        .addConverterFactory(SafeGsonConverterFactory())

    private var retrofit: Retrofit? = null

    private val logging = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.HEADERS)

    /**
     * Create default service with authorization
     *
     * @param serviceClass  service class type
     * @param <S>           Service Class
     * @param authorization Authorization header
     * @param interceptor   custom interceptor
     * @return Service class
    </S> */
    fun <S> createService(
        serviceClass: Class<S>,
        authorization: String = "",
        interceptor: Interceptor? = null
    ): S {
        val httpClient = OkHttpClient.Builder().addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .apply {
                    this.header(HttpHeaders.CONTENT_TYPE, "application/json")
                    authorization.takeIf { it.isNotEmpty() }?.let{
                        this.header(HttpHeaders.AUTHORIZATION, it)
                    }
                    this.header(DEVICE_ID, DeviceUtils.getAndroidID())
                    this.method(original.method, original.body)
                }.build()

            chain.proceed(request)
        }.addInterceptor(UserAgentInterceptor())

        // Timeout out for API
        httpClient.connectTimeout(6, TimeUnit.MINUTES)
        httpClient.readTimeout(6, TimeUnit.MINUTES)
        httpClient.callTimeout(6, TimeUnit.MINUTES)
        httpClient.writeTimeout(6, TimeUnit.MINUTES)


        if (interceptor != null) {
            httpClient.addInterceptor(interceptor)
        }
        if (!httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging)
            builder.client(httpClient.build())
            retrofit = builder.build()
        }
        return retrofit!!.create(serviceClass)
    }

    fun <S> createServiceChatMessage(serviceClass: Class<S>): S {
        builder.baseUrl(SERVER_CHAT_HOST_URI)
        return createService(
            serviceClass = serviceClass,
        )
    }

    fun <S> createMessService(
        serviceClass: Class<S>,
        authorization: String? = ""
    ): S {
        val author = if (authorization.isNullOrEmpty()) {
            ""
        } else {
            "Bearer $authorization"
        }
        builder.baseUrl(MESS_HOST)
        return createService(
            serviceClass = serviceClass,
            authorization = author
        )
    }
}
