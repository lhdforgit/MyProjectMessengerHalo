/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.api.gateway

import com.halo.data.api.utils.ServiceGenerator
import com.halo.data.entities.gateway.GateWay
import com.halo.data.entities.message.Message
import com.halo.data.entities.reaction.ReactionBody
import com.halo.data.entities.reaction.Reactions
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author ndn
 * Created by ndn
 * Created on 9/6/18
 */
@Singleton
class GateWayRestApiImpl @Inject
internal constructor() : GateWayRestApi {

    override suspend fun gateWay(): GateWay? = ServiceGenerator.createMessService(
        serviceClass = GatewayService::class.java,
        authorization = ""
    ).gateWay()
}