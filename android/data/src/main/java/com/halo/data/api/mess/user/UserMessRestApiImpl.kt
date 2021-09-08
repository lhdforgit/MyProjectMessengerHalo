/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.api.mess.user

import com.halo.data.api.utils.ServiceGenerator
import com.halo.data.common.resource.Resource
import com.halo.data.entities.user.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author ndn
 * Created by ndn
 * Created on 5/15/18.
 */
@Singleton
class UserMessRestApiImpl
@Inject constructor() : UserMessRestApi {

    override suspend fun user(
        id: String?,
        token: String?
    ) = kotlin.run {
        val service: UserMessService =
            ServiceGenerator.createMessService(
                serviceClass = UserMessService::class.java,
                authorization = token
            )
        if (id.isNullOrEmpty()) {
            service.user()
        } else {
            service.user(id)
        }
    }

    override suspend fun searchUser(
        token: String,
        keyword: String,
        limit: String
    ) = ServiceGenerator.createMessService(
        serviceClass = UserMessService::class.java,
        authorization = token
    ).searchUser(keyword, limit)
}