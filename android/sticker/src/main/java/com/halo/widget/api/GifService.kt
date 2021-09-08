/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.api

import com.google.common.net.HttpHeaders
import com.halo.widget.model.GifModel

import com.halo.widget.model.StickerCollection
import com.halo.widget.model.StickerModel
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * @author ngannd
 * Create by ngannd on 24/05/2019
 */
interface GifService {
    @GET("v2/crawler/gif/trending")
    suspend fun trending(@Header(HttpHeaders.AUTHORIZATION) header:String): MutableList<GifModel>?

    @GET("v2/crawler/gif/search?")
    suspend fun search(@Header(HttpHeaders.AUTHORIZATION) header:String, @Query("q") query: String): MutableList<GifModel>?

    @GET("v2/crawler/gif/categories")
    suspend fun categories(@Header(HttpHeaders.AUTHORIZATION) header:String): MutableList<GifModel>?
}
