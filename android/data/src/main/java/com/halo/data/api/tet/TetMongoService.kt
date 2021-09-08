/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.api.tet

import androidx.lifecycle.LiveData

import com.halo.data.api.utils.ApiResponse
import com.halo.data.entities.mongo.tet.GifCard

import retrofit2.http.GET

/**
 * @author ngannd
 * Create by ngannd on 24/05/2019
 */
interface TetMongoService {


    @GET("api-rest-service/service/json/giftCarts")
    fun gifCards(): LiveData<ApiResponse<List<GifCard>>>
}
