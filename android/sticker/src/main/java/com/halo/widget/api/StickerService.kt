/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.api

import com.google.common.net.HttpHeaders

import com.halo.widget.model.StickerCollection
import com.halo.widget.model.StickerModel
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

/**
 * @author ngannd
 * Create by ngannd on 24/05/2019
 */
interface StickerService {
    @GET("v2/stickers/collections")
    suspend fun collections(@Header(HttpHeaders.AUTHORIZATION) header:String): MutableList<StickerCollection>?

    @GET("v2/stickers/collections/{collectionId}/stickers")
    suspend fun stickers(@Header(HttpHeaders.AUTHORIZATION) header:String,
                         @Path("collectionId") collectionId:String
                         ): MutableList<StickerModel>?
}
