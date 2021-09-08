/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.api.tet

import com.halo.data.api.utils.ServiceGenerator
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author ndn
 * Created by ndn
 * Created on 9/6/18
 */
@Singleton
class TetMongoRestApiImpl @Inject
internal constructor() : TetMongoRestApi {

    private val service: TetMongoService =
        ServiceGenerator.createService(TetMongoService::class.java)

    override fun gifCards() = service.gifCards()
}