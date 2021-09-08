/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.api.gateway

import com.halo.data.entities.gateway.GateWay
import com.halo.data.entities.message.Message
import com.halo.data.entities.reaction.ReactionBody
import com.halo.data.entities.reaction.Reactions
import retrofit2.http.*

/**
 * @author ngannd
 * Create by ngannd on 24/05/2019
 */
interface GatewayService {

    @GET("/v2/gateway")
    suspend fun gateWay(): GateWay?
}
