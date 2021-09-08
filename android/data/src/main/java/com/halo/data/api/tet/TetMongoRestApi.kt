/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.api.tet

import androidx.lifecycle.LiveData
import com.halo.data.api.utils.ApiResponse
import com.halo.data.entities.mongo.tet.GifCard

/**
 * @author ndn
 * Created by ndn
 * Created on 8/21/18
 */
interface TetMongoRestApi {
    fun gifCards(): LiveData<ApiResponse<List<GifCard>>>
}
