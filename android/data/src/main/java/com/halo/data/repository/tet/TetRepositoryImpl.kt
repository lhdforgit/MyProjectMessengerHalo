/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.repository.tet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.halo.data.api.tet.TetMongoRestApi
import com.halo.data.common.resource.Resource
import com.halo.data.common.resource.StatusNetwork.ERROR
import com.halo.data.entities.mongo.tet.GifCard
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author ngannd
 * Create by ngannd on 24/05/2019
 */
@Singleton
class TetRepositoryImpl @Inject
internal constructor(
    private val mongoRestApi: TetMongoRestApi
) : TetRepository {

    override fun gifCards(): LiveData<Resource<List<GifCard>>> {
        val result = MediatorLiveData<Resource<List<GifCard>>>()
        result.value = Resource.loading(null)
        val api = mongoRestApi.gifCards()
        result.addSource(api) { response ->
            when {
                response == null -> result.setValue(
                    Resource.error(
                        ERROR,
                        "Response null",
                        null
                    )
                )
                response.body != null -> result.setValue(Resource.success(response.body))
                else -> result.setValue(
                    Resource.error(
                        response.code,
                        response.errorMessage ?: "",
                        null
                    )
                )
            }
        }
        return result
    }
}