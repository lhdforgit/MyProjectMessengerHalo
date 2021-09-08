/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.repository.tet

import androidx.lifecycle.LiveData
import com.halo.data.common.resource.Resource
import com.halo.data.entities.mongo.tet.GifCard

/**
 * @author ngannd
 * Create by ngannd on 24/05/2019
 */
interface TetRepository {
    fun gifCards(): LiveData<Resource<List<GifCard>>>
}
