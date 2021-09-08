/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.api.mess.user

import com.halo.data.common.resource.Resource
import com.halo.data.entities.response.ResponsePaging
import com.halo.data.entities.user.User
import kotlinx.coroutines.flow.Flow

/**
 * @author ndn
 * Created by ndn
 * Created on 5/15/18.
 */
interface UserMessRestApi {
    suspend fun user(
        id: String?,
        token: String?
    ): User?

    suspend fun searchUser(
        token: String,
        keyword : String,
        limit : String
    ) : ResponsePaging<User>?
}